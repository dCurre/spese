package com.dcapps.spese.views.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcapps.spese.R
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.databinding.JoinFragmentBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.enums.entity.ExpensesListFieldsEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.SnackbarUtils
import com.dcapps.spese.views.MainActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


class JoinFragment : Fragment(R.layout.join_fragment) {

    private val className = javaClass.simpleName
    private var _binding: JoinFragmentBinding? = null
    private var currentUser: FirebaseUser = DBUtils.getLoggedUser()
    private lateinit var expensesListViewModel : ExpensesListViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = JoinFragmentBinding.inflate(inflater, container, false)
        currentUser = DBUtils.getLoggedUser()
        expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]

        //Nascondo bottom nav
        (activity as MainActivity).setBottomNavVisibility(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadJoinGroupDetails()

        setupJoinButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadJoinGroupDetails(){
        expensesListViewModel.findByID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpese ->

            if (listaSpese != null) {
                binding.nomeGruppo.text = listaSpese.name
                binding.counterCurrentUsers.text = listaSpese.partecipatingUsersID!!.size.toString()
            }
            binding.counterMaxUsers.text = "8"
            binding.joinListaButton.text = "Unisciti"
        }
    }

    private fun setupJoinButton(){
        val idLista = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString().replace(" ", "")

        binding.joinListaButton.setOnClickListener {
            expensesListViewModel.findByID(idLista)
            expensesListViewModel.expensesListLiveData.observeOnce { expensesList ->
                //Se l'utente è già presente in lista
                if (expensesList != null) {
                    if(expensesList.partecipatingUsersID!!.contains(currentUser.uid)){
                        SnackbarUtils.showSnackbarError("Fai già parte della lista!", binding.root)
                        return@observeOnce
                    }

                    if(expensesList.partecipatingUsersID.size >= binding.counterMaxUsers.text.toString().toInt()) {
                        SnackbarUtils.showSnackbarError(
                            "Numero massimo di utenti raggiunto!",
                            binding.root
                        )
                        return@observeOnce
                    }

                    expensesList.partecipatingUsersID.add(currentUser.uid)

                    expensesListViewModel.updateByField(idLista, ExpensesListFieldsEnum.PARTECIPATING_USERS_ID.value, expensesList.partecipatingUsersID)

                    //The user gets also added to the notification topic of this list
                    Firebase.messaging.subscribeToTopic(expensesList.id!!)

                    SnackbarUtils.showSnackbarOKOverBottomnav("Ti sei aggiunto alla lista ${expensesList.name}", binding.root)
                    findNavController().popBackStack()
                }
            }
        }
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