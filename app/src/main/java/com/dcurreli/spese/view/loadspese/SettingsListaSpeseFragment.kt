package com.dcurreli.spese.view.loadspese

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.PartecipantiAdapter
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.viewmodel.ExpenseViewModel
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.ListaSettingsFragmentBinding
import com.dcurreli.spese.enums.entity.ExpensesListFieldEnum
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.ExcelUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SnackbarUtils
import com.dcurreli.spese.view.MainActivity
import com.google.firebase.auth.FirebaseUser
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

        val idLista = arguments?.getString("idLista").toString()
        val nomeLista = arguments?.getString("nomeLista").toString()

        printPartecipanti(idLista)

        setupSwitches(idLista)

        setupButtons(idLista, nomeLista)

        setupToolbar(nomeLista)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun printPartecipanti(idLista: String) {
        val partecipantiAdapter = PartecipantiAdapter()
        expensesListViewModel.findByID(idLista)
        userViewModel.findAll()
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpese ->
            val partecipantiArray = ArrayList<User>()
            userViewModel.userListLiveData.observe(viewLifecycleOwner) { userList ->
                //TODO filtrare nella query per user id
                userList.forEach { user ->
                    if(listaSpese.partecipatingUsersID!!.contains(user.id)){
                        //Se l'utente è anche owner lo aggiungo in cima
                        if(listaSpese.owner!!.contains(user.id)){
                            partecipantiArray.add(0, user)
                        } else {
                            partecipantiArray.add(user)
                        }
                    }
                }
                partecipantiAdapter.addItems(partecipantiArray)
            }
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
            binding.buttonDelete.visibility = if (listaSpese.owner.equals(currentUser.uid)) View.VISIBLE else View.GONE
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
        var expensesList = ExpensesList(null, null, null, null, false, null)
        this.expensesListViewModel.findByID(idLista)
        this.expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { extractedExpensesList ->
            expensesList = extractedExpensesList
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
                .setPositiveButton("SI") { _, _ -> leaveListAndChangeOwnership(expensesList) }
                .setNegativeButton("NO") { _, _ -> }
                .show()
        }
    }

    private fun leaveListAndChangeOwnership(expensesList: ExpensesList) {
        expensesList.partecipatingUsersID!!.remove(currentUser.uid)

        //Se ero l'owner allora passo l'ownership al primo della lista
        if(expensesList.owner.equals(currentUser.uid, ignoreCase = true)){
            this.expensesListViewModel.updateByField(expensesList.id!!,ExpensesListFieldEnum.OWNER.value, expensesList.partecipatingUsersID[0])
        }

        this.expensesListViewModel.updateByField(expensesList.id!!,ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, expensesList.partecipatingUsersID)

        //REDIRECT MAIN PAGE
        activity?.finish()
        startActivity(Intent(Intent(context, MainActivity::class.java)))
    }

    private fun setupSwitches(idLista: String) {

        expensesListViewModel.findByID(idLista)
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpese ->
            GenericUtils.setupSwitch(binding.switchPaid, listaSpese.paid)
        }

        binding.switchPaid.setOnCheckedChangeListener { _, bool ->
            expensesListViewModel.updateByField(idLista, ExpensesListFieldEnum.PAID.value, bool)
        }
    }

    private fun setupToolbar(nomeLista: String) {
        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle("Impostazioni $nomeLista")
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }
}