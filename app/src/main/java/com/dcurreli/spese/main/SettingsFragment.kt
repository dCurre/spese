package com.dcurreli.spese.main

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.databinding.SettingsFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.login.LoginActivity
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: SettingsFragmentBinding? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db = DBUtils.getDatabaseReference(TablesEnum.UTENTE)
    private lateinit var user : FirebaseUser
    private lateinit var mAuth: FirebaseAuth

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SettingsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = DBUtils.getCurrentUser()!!
        mAuth = DBUtils.getAuthentication()
        // Configure Google Sign out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        //Carico i dati dal db per lo switch del tema
        setupSwitches()

        //Carico foto e nome utente
        setupUserImage()

        binding.switchDarkTheme.setOnCheckedChangeListener { _, checkedId ->
            GenericUtils.onOffDarkTheme(db, user, checkedId)
        }

        binding.switchNascondiListe.setOnCheckedChangeListener { _, checkedId ->
            GenericUtils.onOffNascondiListe(db, user, checkedId)
        }

        binding.signOutBtn.setOnClickListener {
            signOut()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut(){
        startActivity(Intent(Intent(context, LoginActivity::class.java)))
        activity?.finish()
        mAuth.signOut()
        googleSignInClient.signOut()
    }

    private fun setupSwitches(){
        db.child(user.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val user : User = it.getValue(
                    User::class.java) as User
                GenericUtils.setupSwitch(binding.switchDarkTheme, user.isDarkTheme)
                GenericUtils.setupSwitch(binding.switchNascondiListe, user.isNascondiListeSaldate)
            }
        }.addOnFailureListener {
            Log.e(ContentValues.TAG, "<<Error getting utente", it)
        }
    }

    private fun setupUserImage() {
        val user = DBUtils.getCurrentUser()

        binding.userName.text = user?.displayName
        Picasso.get()
            .load(DBUtils.getCurrentUserImage().replace("=s96-c", ""))
            .error( R.drawable.ic_close )
            .placeholder( R.drawable.loading_animation )
            .into(binding.settingsUserImage)
    }
}