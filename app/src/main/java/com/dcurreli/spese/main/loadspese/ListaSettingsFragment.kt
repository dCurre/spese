package com.dcurreli.spese.main.loadspese

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ListaSettingsFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ListaSettingsFragment : Fragment(R.layout.settings_fragment) {

    private val TAG = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ListaSettingsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwitchTheme()

        binding.buttonAbbandona.setOnClickListener {
            leaveLista()
        }

        binding.switchSaldato.setOnCheckedChangeListener { _, checkedId ->
            GenericUtils.onOffSaldato(db, arguments?.getString("idLista").toString(), checkedId)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun leaveLista(){
    }

    private fun setupSwitchTheme(){
        if (arguments != null) {
            db.child(arguments?.getString("idLista").toString()).get().addOnSuccessListener {
                //Se non esiste creo l'utente nella lista utenti
                if (it.exists()) {
                    val lista : ListaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                    when (lista.isSaldato) {
                        true -> binding.switchSaldato.isChecked = true
                        false -> binding.switchSaldato.isChecked = false
                    }
                }
            }.addOnFailureListener {
                Log.e(TAG, "<<Error getting utente", it)
            }
        }
    }
}