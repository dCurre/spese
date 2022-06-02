package com.dcurreli.spese.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.GenericUtils.dateToTimestampSeconds
import com.dcurreli.spese.utils.SnackbarUtils
import java.util.*

class NuovaListaSpeseFragment : Fragment(R.layout.add_lista_spese) {

    private val className = javaClass.simpleName
    private var _binding: AddListaSpeseBinding? = null
    private val binding get() = _binding!!
    private val currentUser = DBUtils.getLoggedUser()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddListaSpeseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se premo lo sfondo
        binding.addListaSpeseConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            GenericUtils.clearTextViewFocus(binding) //Tolgo il focus dagli altri bottoni
        }

        setupAddButton(view) //Setup bottone "Aggiungi"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAddButton(view: View) {
        binding.spesaButtonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            if (binding.listaSpeseNomeText.text.isNullOrBlank()) {
                SnackbarUtils.showSnackbarError("Nome lista non inserito !", binding.addListaSpeseConstraintLayout)
            } else {

                //Insert nuova lista spese
                ViewModelProvider(this)[ExpensesListViewModel::class.java]
                    .insert(
                        ExpensesList(
                            DBUtils.getDatabaseReferenceFirestore(TablesEnum.EXPENSES_LISTS).document().id,
                            binding.listaSpeseNomeText.text.toString().trim(),
                            arrayListOf(currentUser.uid),
                            currentUser.uid,
                            paid = false,
                            dateToTimestampSeconds(Date())
                        )
                    )

                SnackbarUtils.showSnackbarOKOverBottomnav("Lista creata : )", binding.addListaSpeseConstraintLayout)

                findNavController().navigate(R.id.action_addListaSpeseFragment_to_homeFragment)
            }
        }
    }
}