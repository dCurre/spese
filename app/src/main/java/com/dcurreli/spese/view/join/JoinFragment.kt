package com.dcurreli.spese.view.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.enums.entity.ExpensesListFieldEnum
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.SnackbarUtils
import com.dcurreli.spese.view.MainActivity
import com.google.firebase.auth.FirebaseUser


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
        expensesListViewModel.findByID(arguments?.getString("idLista").toString())
        expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpese ->
            binding.nomeGruppo.text = listaSpese.name
            binding.counterCurrentUsers.text = listaSpese.partecipatingUsersID!!.size.toString()
            binding.counterMaxUsers.text = "8"
            binding.joinListaButton.text = "Unisciti"
        }
    }

    private fun setupJoinButton(){
        val idLista = arguments?.getString("idLista").toString().replace(" ", "")

            expensesListViewModel.findByID(idLista)
            expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
                binding.joinListaButton.setOnClickListener {
                    //Se l'utente è già presente in lista
                    if(expensesList.partecipatingUsersID!!.contains(currentUser.uid)){
                        SnackbarUtils.showSnackbarError("Fai già parte della lista!", binding.root)
                        return@setOnClickListener
                    }

                    //Se ho raggiunto il massimo numero di utenti
                    if(expensesList.partecipatingUsersID.size >= binding.counterMaxUsers.text.toString().toInt()){
                        SnackbarUtils.showSnackbarError("Numero massimo di utenti raggiunto!", binding.root)
                        return@setOnClickListener
                    }

                    expensesList.partecipatingUsersID.add(currentUser.uid)

                    expensesListViewModel.updateByField(idLista, ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, expensesList.partecipatingUsersID)

                    SnackbarUtils.showSnackbarOKOverBottomnav("Ti sei aggiunto alla lista ${expensesList.name}", binding.root)
                    findNavController().popBackStack()
                }
            }
    }
}