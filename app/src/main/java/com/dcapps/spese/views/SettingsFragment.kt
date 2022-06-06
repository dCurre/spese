package com.dcapps.spese.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dcapps.spese.R
import com.dcapps.spese.data.viewmodels.UserViewModel
import com.dcapps.spese.databinding.SettingsFragmentBinding
import com.dcapps.spese.enums.entity.UserFieldsEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.views.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private var _binding: SettingsFragmentBinding? = null
    private lateinit var currentUser : FirebaseUser
    private lateinit var userModel : UserViewModel

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

        //Setting up switches' status by retrieving data from firestore
        setupSwitches()

        //Setting up user name and profile image
        setupUserImage()

        //Manages all the buttons
        manageButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun manageButtons() {

        //Changes the theme
        binding.switchDarkTheme.setOnCheckedChangeListener { _, bool ->
            userModel.updateByField(currentUser.uid, UserFieldsEnum.DARKTHEME.value, bool)
        }

        //Changes the visibility of paid lists
        binding.switchHidePaidLists.setOnCheckedChangeListener { _, bool ->
            userModel.updateByField(currentUser.uid, UserFieldsEnum.HIDE_PAID_LISTS.value, bool)
        }

        //Sign out on click
        signOut()
    }

    private fun signOut() {
        //Logs the user out
        binding.signOutBtn.setOnClickListener {
            //LOGIN Redirection
            startActivity(Intent(Intent(context, LoginActivity::class.java)))
            activity?.finish()

            DBUtils.getAuthentication().signOut()

            // Configure Google Sign out
            GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build())
                .signOut()
        }
    }

    private fun setupSwitches(){
        userModel.findByID(currentUser.uid)
        userModel.userLiveData.observe(viewLifecycleOwner) { user ->
            GenericUtils.setupSwitch(binding.switchDarkTheme, user.darkTheme)
            GenericUtils.setupSwitch(binding.switchHidePaidLists, user.hidePaidLists)
            GenericUtils.onOffDarkTheme(user.darkTheme)
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