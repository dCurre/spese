package com.dcapps.spese.views.loadspese

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcapps.spese.R
import com.dcapps.spese.adapters.PartecipantiAdapter
import com.dcapps.spese.constants.FirebaseConstants
import com.dcapps.spese.data.dto.notification.NotificationData
import com.dcapps.spese.data.dto.notification.NotificationMessage
import com.dcapps.spese.data.entities.ExpensesList
import com.dcapps.spese.data.entities.User
import com.dcapps.spese.data.viewmodels.ExpenseViewModel
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.data.viewmodels.UserViewModel
import com.dcapps.spese.databinding.ListaSettingsFragmentBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.enums.entity.ExpensesListFieldsEnum
import com.dcapps.spese.services.CustomFirebaseMessagingService
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.ExcelUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.utils.SnackbarUtils
import com.dcapps.spese.views.MainActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class SettingsListaSpeseFragment : Fragment(R.layout.lista_settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private lateinit var userViewModel : UserViewModel
    private lateinit var expensesListViewModel : ExpensesListViewModel
    private lateinit var expenseViewModel : ExpenseViewModel
    private lateinit var currentUser : FirebaseUser

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        currentUser = DBUtils.getLoggedUser()
        _binding = ListaSettingsFragmentBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idLista = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString()
        val nomeLista = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_NAME.value).toString()
        val owner = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_OWNER.value).toString()

        printPartecipanti(idLista, owner)

        setupSwitches(idLista)

        setupButtons(idLista, nomeLista)

        setupToolbar(nomeLista)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun printPartecipanti(idLista: String, owner: String) {
        val partecipantiAdapter = PartecipantiAdapter()

        expensesListViewModel.findByID(idLista)
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
            if(expensesList == null)
                return@observe

            userViewModel.findAllByUserIdList(expensesList.partecipatingUsersID!!)
        }

        userViewModel.userListLiveData.observe(viewLifecycleOwner) { userList ->
            val partecipantiArray = ArrayList<User>()

            userList.forEach { user ->
                //Se l'utente è anche owner lo aggiungo in cima
                if(user.id == owner){
                    partecipantiArray.add(0, user)
                } else {
                    partecipantiArray.add(user)
                }
            }
            partecipantiAdapter.addItems(partecipantiArray)
        }
        binding.listaPartecipanti.adapter = partecipantiAdapter
        binding.listaPartecipanti.layoutManager = LinearLayoutManager(context)
    }

    private fun setupButtons(idLista: String, nomeLista:  String) {

        showHideDeleteButton(idLista)

        binding.buttonDelete.setOnClickListener {
            deleteList(idLista)
        }

        binding.buttonLeave.setOnClickListener {
            leaveList(idLista)
        }

        binding.buttonEsportaLista.setOnClickListener {
            esportaLista(idLista, nomeLista)
        }
    }

    private fun showHideDeleteButton(idLista: String) {
        expensesListViewModel.findByID(idLista)
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpese ->
            binding.buttonDelete.visibility = if (currentUser.uid == listaSpese?.owner) View.VISIBLE else View.GONE
        }
    }

    private fun deleteList(idLista: String) {

        AlertDialog.Builder(context)
            .setTitle("Conferma")
            .setMessage("Vuoi veramente cancellare la lista?")
            .setPositiveButton("SI") { _, _ ->

                //ELIMINO LE SPESE LEGATE A QUELLA LISTA
                expenseViewModel.findAllByExpensesListID(idLista)
                expenseViewModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->

                    //Deleting every expense by idList
                    expenseViewModel.deleteList(expenseList)

                    //Deleting the list
                    expensesListViewModel.delete(idLista)

                    //The user gets also unsubscribed from push notifications related to this list
                    Firebase.messaging.unsubscribeFromTopic(idLista)

                    //REDIRECT MAIN PAGE
                    activity?.finish()
                    startActivity(Intent(Intent(context, MainActivity::class.java)))
                }
            }
            .setNegativeButton("NO") { _, _ -> }
            .show()
    }

    private fun esportaLista(idLista: String, nomeLista: String) {
        //Se l'utente non ha i permessi non posso scaricare il file
        (activity as MainActivity).checkUserPermission()

        val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val completeFileName = "Riepilogo_spese_$nomeLista.xlsx"
        val completePath = "$downloadFolder/$completeFileName"

        expenseViewModel.findAllByExpensesListID(idLista)
        expenseViewModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->
            val hssfWorkbook = HSSFWorkbook()
            val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(nomeLista)
            var rowCounter = 1

            //Testata del file
            ExcelUtils.printHeaderRow(0, hssfSheet)

            //Ciclo per ottenere spese e le stampo
            for (expense in expenseList) {
                ExcelUtils.printRow(rowCounter, expense.expense, expense.amount, expense.expenseDate, expense.buyer, hssfSheet)
                rowCounter++
            }

            //Stampo la somma degli importi ad una riga di distanza
            ExcelUtils.printFormula(rowCounter+1, "Totale", "SUM(B1:B$rowCounter)", hssfSheet)

            //Creo il file
            try {
                val fileOutputStream = FileOutputStream(File(completePath))
                hssfWorkbook.write(fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()

                SnackbarUtils.showSnackbarOpenPath("\"$completeFileName\" scaricato in $downloadFolder", binding.root, downloadFolder, requireContext())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun leaveList(idLista: String){
        var expensesList = ExpensesList()
        this.expensesListViewModel.findByID(idLista)
        this.expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { extractedExpensesList ->
            expensesList = extractedExpensesList ?: ExpensesList()
        }

        if(expensesList.partecipatingUsersID!!.size == 1 && expensesList.partecipatingUsersID!![0].equals(currentUser.uid, ignoreCase = true)){
            AlertDialog.Builder(context)
                .setTitle("Conferma")
                .setMessage("Sei l'unico partecipante di questa lista, vuoi cancellarla?")
                .setPositiveButton("SI") { _, _ -> deleteList(idLista) }
                .setNegativeButton("NO") { _, _ -> }
                .show()
        } else {
            AlertDialog.Builder(context)
                .setTitle("Conferma")
                .setMessage("Vuoi veramente abbandonare la lista?")
                .setPositiveButton("SI") { _, _ -> leaveListAndChangeOwnership(expensesList)  }
                .setNegativeButton("NO") { _, _ -> }
                .show()
        }
    }

    private fun leaveListAndChangeOwnership(expensesList: ExpensesList) {
        expensesList.partecipatingUsersID!!.remove(currentUser.uid)

        //Se ero l'owner allora passo l'ownership al primo della lista
        if(expensesList.owner.equals(currentUser.uid, ignoreCase = true)){
            this.expensesListViewModel.updateByField(expensesList.id!!,ExpensesListFieldsEnum.OWNER.value, expensesList.partecipatingUsersID[0])
        }

        this.expensesListViewModel.updateByField(expensesList.id!!,ExpensesListFieldsEnum.PARTECIPATING_USERS_ID.value, expensesList.partecipatingUsersID)

        //The user gets also unsubscribed from push notifications related to this list
        Firebase.messaging.unsubscribeFromTopic(expensesList.id)

        //REDIRECT MAIN PAGE
        activity?.finish()
        startActivity(Intent(Intent(context, MainActivity::class.java)))
    }

    private fun setupSwitches(idLista: String) {

        expensesListViewModel.findByID(idLista)
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
            GenericUtils.setupSwitch(binding.switchPaid, expensesList?.paid ?: false)
        }

        binding.switchPaid.setOnCheckedChangeListener { _, bool ->
            userViewModel.findByID(DBUtils.getLoggedUser().uid)
            userViewModel.userLiveData.observeOnce { user ->
                expensesListViewModel.expensesListLiveData.observeOnce { expensesList ->
                    if (expensesList != null && !expensesList.paid) {
                        //Notifico gli che la lista è stata saldata
                        CustomFirebaseMessagingService.sendNotification(
                            NotificationMessage(
                                NotificationData(
                                    title = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_NAME.value).toString(),
                                    body = "è stata saldata da ${user.fullname}",
                                    sender = user.id
                                ),
                                to = "${FirebaseConstants.TOPICS}/${arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString()}")
                        )
                    }
                }
                expensesListViewModel.updateByField(idLista, ExpensesListFieldsEnum.PAID.value, bool)
            }
        }
    }

    private fun setupToolbar(nomeLista: String) {
        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle("Impostazioni $nomeLista")
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object: Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
}