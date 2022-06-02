package com.dcurreli.spese.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.SettingsFragmentBinding
import com.dcurreli.spese.view.login.LoginActivity
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: SettingsFragmentBinding? = null
    private lateinit var currentUser : FirebaseUser
    private lateinit var userModel : UserViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
        currentUser = DBUtils.getLoggedUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.switchDarkTheme.setOnCheckedChangeListener { _, bool ->
            userModel.updateByField(currentUser.uid, "darkTheme", bool)
            GenericUtils.onOffDarkTheme(bool)
        }

        binding.switchNascondiListe.setOnCheckedChangeListener { _, bool ->
            userModel.updateByField(currentUser.uid, "nascondiListeSaldate", bool)
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
        DBUtils.getAuthentication().signOut()
        googleSignInClient.signOut()
    }

    private fun setupSwitches(){
        userModel.findById(currentUser.uid)
        userModel.userLiveData.observe(viewLifecycleOwner) { user ->
            GenericUtils.setupSwitch(binding.switchDarkTheme, user.isDarkTheme)
            GenericUtils.setupSwitch(binding.switchNascondiListe, user.isNascondiListeSaldate)
        }
    }

    private fun setupUserImage() {
        binding.userName.text = currentUser.displayName
        Picasso.get()
            .load(currentUser.photoUrl.toString().replace("=s96-c", ""))
            .error(R.drawable.ic_close)
            .placeholder(R.drawable.loading_animation)
            .into(binding.settingsUserImage)
    }
}