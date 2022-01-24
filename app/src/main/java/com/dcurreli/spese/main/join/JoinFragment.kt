package com.dcurreli.spese.main.join

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.utils.ListaSpeseUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class JoinFragment : Fragment(R.layout.join_fragment) {

    private var _binding: JoinFragmentBinding? = null
    private val TAG = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value.lowercase())

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = JoinFragmentBinding.inflate(inflater, container, false)
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
        val idLista = arguments?.getString("idLista").toString()

        db.orderByChild("id").equalTo(idLista.replace(" ", "")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val listaSpese = it.children.first().getValue(ListaSpese::class.java) as ListaSpese //singola lista spesa cercata tramite id specifico

                    binding.nomeGruppo.text = listaSpese.nome
                    binding.counterCurrentUsers.text = listaSpese.partecipanti.size.toString()
                    binding.counterMaxUsers.text = "8"
                    binding.joinListaButton.text = "Unisciti"
                }else{
                    Log.i(TAG, ">>Non esiste una lista ")
                }
            }.addOnFailureListener {
                Log.e(TAG, "<< Error getting mese", it)
            }
    }

    private fun setupJoinButton(){
        binding.joinListaButton.setOnClickListener {

            //TODO gestire con un adapter

            //In base ad un id list aggiunge un utente ad una spesa
            ListaSpeseUtils.joinListaSpese(arguments?.getString("idLista").toString(), binding, findNavController())
        }
    }

}