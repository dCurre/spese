package com.dcapps.spese.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcapps.spese.R
import com.dcapps.spese.data.entity.Expense
import com.dcapps.spese.data.viewmodel.ExpenseViewModel
import com.dcapps.spese.data.viewmodel.ExpensesListViewModel
import com.dcapps.spese.data.viewmodel.UserViewModel
import com.dcapps.spese.databinding.AddSpesaBinding
import com.dcapps.spese.enums.bundle.BundleArgumentEnum
import com.dcapps.spese.enums.table.TablesEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.DateUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.utils.GenericUtils.dateStringToTimestampSeconds
import com.dcapps.spese.utils.SnackbarUtils
import java.util.*


class NuovaSpesaFragment : Fragment(R.layout.add_spesa) {

    private val className = javaClass.simpleName
    private var _binding: AddSpesaBinding? = null
    private lateinit var userModel : UserViewModel
    private lateinit var expensesListViewModel : ExpensesListViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddSpesaBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
        expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbar()

        //Setup calendario
        setupCalendario()

        //Setup autocomplete fields
        setupAutocompleteInputs()

        //Se premo lo sfondo
        binding.addSpesaConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            GenericUtils.clearTextViewFocusAddSpesa(binding) //Tolgo il focus dagli altri bottoni
        }

        //Bottone "Aggiungi"
        binding.spesaButtonAddSpesa.setOnClickListener {
            insertExpense(view)
        }

        //TODO: tasto debug da togliere
        if(!DBUtils.getLoggedUser().email.equals("curre994@gmail.com", ignoreCase = true)){
            binding.spesaButtonAddSpesa10.visibility = View.GONE
        }
        //TODO: tasto debug da togliere
        binding.spesaButtonAddSpesa10.setOnClickListener {
            for(i in 0 until 10){
                insertExpense(view)
            }
        }
    }

    private fun insertExpense(view: View) {
        //Chiudo la tastiera come prima cosa
        GenericUtils.hideSoftKeyBoard(requireContext(), view)

        when {
            binding.spesaSpesaText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo spesa non popolato !", binding.addSpesaConstraintLayout) }
            binding.spesaImporto.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo importo non popolato !", binding.addSpesaConstraintLayout) }
            binding.spesaData.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo data non popolato !", binding.addSpesaConstraintLayout) }
            binding.spesaPagatoreText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo pagatore non popolato !", binding.addSpesaConstraintLayout) }
            binding.spesaImporto.text.toString().toDouble().equals(0.00) -> { SnackbarUtils.showSnackbarError("Inserire importo maggiore di 0 !", binding.addSpesaConstraintLayout) }
            else -> {
                expenseViewModel.insert(
                    Expense(
                        DBUtils.getFirestoreReference(TablesEnum.EXPENSE).document().id,
                        binding.spesaSpesaText.text.toString().trim(),
                        binding.spesaImporto.text.toString().trim().replace(",", ".").toDouble(),
                        binding.spesaData.text.toString().trim(),
                        expenseDateTimestamp = dateStringToTimestampSeconds(binding.spesaData.text.toString().trim()),
                        binding.spesaPagatoreText.text.toString().trim(),
                        arguments?.getString(BundleArgumentEnum.EXPENSES_LIST_ID.value).toString()
                    )
                )

                SnackbarUtils.showSnackbarOK("Spesa creata : )", binding.addSpesaConstraintLayout)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAutocompleteInputs() {
        val spesaText = binding.spesaSpesaText
        val pagatoreText = binding.spesaPagatoreText
        val arrayAdapterSpese = ArrayAdapter<String>(requireContext(), R.layout.add_spesa_custom_spinner)
        val arrayAdapterPagatori = ArrayAdapter<String>(requireContext(), R.layout.add_spesa_custom_spinner)
        val pagatoriList = ArrayList<String>()

        setupAutocompleteInputSpese(pagatoriList, arrayAdapterSpese, arrayAdapterPagatori)

        setupAutocompleteInputPagatori(pagatoriList, arrayAdapterPagatori)

        spesaText.setAdapter(arrayAdapterSpese)
        pagatoreText.setAdapter(arrayAdapterPagatori)

    }

    private fun setupAutocompleteInputPagatori(
        pagatoriList: ArrayList<String>,
        arrayAdapterPagatori: ArrayAdapter<String>
    ) {
        expensesListViewModel.findByID(arguments?.getString(BundleArgumentEnum.EXPENSES_LIST_ID.value).toString())
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
            val partecipanti = expensesList?.partecipatingUsersID
            var countPartecipanti = partecipanti!!.size

            //TODO DA SISTEMARE DOPO CAMBIO A FIRESTORE
            userModel.findAllByUserIdList(partecipanti)
            userModel.userListLiveData.observe(viewLifecycleOwner) { userList ->
                for(user in userList){
                    countPartecipanti--

                    //Recuperato l'utente lo aggiungo alla lista
                    pagatoriList.add(user.fullname)

                    //Quando il contatore raggiunge lo 0, faccio la distinct per filtrarmi i doppioni e aggiungo alla lista
                    if(countPartecipanti == 0){
                        arrayAdapterPagatori.addAll(pagatoriList.distinct())
                    }
                }
            }
        }
    }

    private fun setupAutocompleteInputSpese(
        pagatoriList: ArrayList<String>,
        arrayAdapterSpese: ArrayAdapter<String>,
        arrayAdapterPagatori: ArrayAdapter<String>
    ) {

        expenseViewModel.findAllByExpensesListID(arguments?.getString(BundleArgumentEnum.EXPENSES_LIST_ID.value).toString())
        expenseViewModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->
            val speseList = ArrayList<String>()
            pagatoriList.clear()
            arrayAdapterSpese.clear()
            arrayAdapterPagatori.clear()

            //Ciclo per ottenere spese e pagatori
            expenseList.forEach { spesa ->
                speseList.add(spesa.expense)
                pagatoriList.add(spesa.buyer)
            }

            //Faccio la distinct per filtrarmi le spese doppie, i pagatori li aggiungo dopo
            arrayAdapterSpese.addAll(speseList.distinct())
        }
    }

    private fun setupToolbar() {
        val titolo =  if(arguments == null) "Aggiungi una spesa" else "Aggiungi a ${arguments?.getString(BundleArgumentEnum.EXPENSES_LIST_NAME.value).toString()}"

        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle(titolo)
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendario() {
        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //Di base settato a today
        binding.spesaData.setText(DateUtils.formatData(Calendar.getInstance().time))

        binding.spesaData.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                { _, mYear, mMonth, mDay ->
                    //Setto la data nella text view
                    binding.spesaData.setText(
                        "${
                            String.format(
                                "%02d",
                                mDay
                            )
                        }/${String.format("%02d", (mMonth + 1))}/$mYear"
                    )

                    GenericUtils.clearTextViewFocusAddSpesa(binding) //Tolgo il focus dagli altri bottoni
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}