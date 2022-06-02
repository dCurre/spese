package com.dcurreli.spese.main.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.SnackbarUtils
import com.google.firebase.auth.FirebaseUser


class JoinFragment : Fragment(R.layout.join_fragment) {

    private val className = javaClass.simpleName
    private var _binding: JoinFragmentBinding? = null
    private var currentUser: FirebaseUser = DBUtils.getLoggedUser()
    private lateinit var listaSpeseViewModel : ListaSpeseViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = JoinFragmentBinding.inflate(inflater, container, false)
        currentUser = DBUtils.getLoggedUser()
        listaSpeseViewModel = ViewModelProvider(this)[ListaSpeseViewModel::class.java]

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
        listaSpeseViewModel.findById(arguments?.getString("idLista").toString())
        listaSpeseViewModel.listaSpeseLiveData.observe(viewLifecycleOwner) { listaSpese ->
            binding.nomeGruppo.text = listaSpese.nome
            binding.counterCurrentUsers.text = listaSpese.partecipanti.size.toString()
            binding.counterMaxUsers.text = "8"
            binding.joinListaButton.text = "Unisciti"
        }
    }

    private fun setupJoinButton(){
        val idLista = arguments?.getString("idLista").toString().replace(" ", "")

            listaSpeseViewModel.findById(idLista)
            listaSpeseViewModel.listaSpeseLiveData.observe(viewLifecycleOwner) { listaSpese ->
                binding.joinListaButton.setOnClickListener {
                    //Se l'utente è già presente in lista
                    if(listaSpese.partecipanti.contains(currentUser.uid)){
                        SnackbarUtils.showSnackbarError("Fai già parte della lista!", binding.root)
                        return@setOnClickListener
                    }

                    //Se ho raggiunto il massimo numero di utenti
                    if(listaSpese.partecipanti.size >= binding.counterMaxUsers.text.toString().toInt()){
                        SnackbarUtils.showSnackbarError("Numero massimo di utenti raggiunto!", binding.root)
                        return@setOnClickListener
                    }

                    listaSpese.partecipanti.add(currentUser.uid)

                    listaSpeseViewModel.updateByField(idLista, "partecipanti", listaSpese.partecipanti)

                    SnackbarUtils.showSnackbarOKOverBottomnav("Ti sei aggiunto alla lista ${listaSpese.nome}", binding.root)
                    findNavController().popBackStack()
                }
            }
    }
}