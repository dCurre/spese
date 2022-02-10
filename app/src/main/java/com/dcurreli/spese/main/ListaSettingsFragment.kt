package com.dcurreli.spese.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ListaSettingsFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.login.LoginActivity
import com.dcurreli.spese.utils.DBUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ListaSettingsFragment : Fragment(R.layout.settings_fragment) {

    private val TAG = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.UTENTE.value)
    private lateinit var user : FirebaseUser
    private lateinit var mAuth: FirebaseAuth

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
        user = DBUtils.getCurrentUser()!!
        mAuth = DBUtils.getAuthentication()

        binding.buttonAbbandona.setOnClickListener {
            leaveLista()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun leaveLista(){
    }
}