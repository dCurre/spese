package com.dcapps.spese.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcapps.spese.R
import com.dcapps.spese.data.entities.ExpensesList
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.databinding.AddListaSpeseBinding
import com.dcapps.spese.enums.table.TablesEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.utils.GenericUtils.dateToTimestampSeconds
import com.dcapps.spese.utils.SnackbarUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import java.util.*

class NuovaListaSpeseFragment : Fragment(R.layout.add_lista_spese) {

    private val className = javaClass.simpleName
    private var _binding: AddListaSpeseBinding? = null
    private val binding get() = _binding!!
    private val currentUser = DBUtils.getLoggedUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddListaSpeseBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    private fun setupAddButton(view: View) {
        binding.spesaButtonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            if (binding.listaSpeseNomeText.text.isNullOrBlank()) {
                SnackbarUtils.showSnackbarError("Nome lista non inserito !", binding.addListaSpeseConstraintLayout)
            } else {
                val newExpensesListID = DBUtils.getFirestoreReference(TablesEnum.EXPENSES_LISTS).document().id

                //Inserting new ExpensesList
                ViewModelProvider(this)[ExpensesListViewModel::class.java]
                    .insert(
                        ExpensesList(
                            newExpensesListID,
                            binding.listaSpeseNomeText.text.toString().trim(),
                            arrayListOf(currentUser.uid),
                            currentUser.uid,
                            paid = false,
                            dateToTimestampSeconds(Date())
                        )
                    )

                //The user gets also added to the notification topic of this list
                Firebase.messaging.subscribeToTopic(newExpensesListID)

                SnackbarUtils.showSnackbarOKOverBottomnav("Lista creata : )", binding.addListaSpeseConstraintLayout)

                findNavController().navigate(R.id.action_addListaSpeseFragment_to_homeFragment)
            }
        }
    }
}