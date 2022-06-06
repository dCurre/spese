package com.dcapps.spese.views.login

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dcapps.spese.R
import com.dcapps.spese.data.entities.User
import com.dcapps.spese.data.viewmodels.UserViewModel
import com.dcapps.spese.databinding.ActivityLoginBinding
import com.dcapps.spese.enums.entity.UserFieldsEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.views.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private val className = javaClass.simpleName
    private lateinit var binding : ActivityLoginBinding
    private lateinit var userViewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Configure Google Sign In
        signIn(GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        ))
    }

    private fun signIn(googleSignInClient: GoogleSignInClient) {
        binding.signInBtn.setOnClickListener {
            Log.d(className, ">> signIn()")
            activityResultLauncher.launch(Intent(googleSignInClient.signInIntent))
            Log.d(className, "<< signIn()")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        DBUtils.getAuthentication().signInWithCredential(
            GoogleAuthProvider.getCredential(idToken, null))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //LOGIN SUCCESSFUL
                    val loggedUser = DBUtils.getLoggedUser()

                    userViewModel.findByID(loggedUser.uid)
                    userViewModel.userLiveData.observe(this) { user ->
                        if (user == null) {
                            //CREATING THE USER CAUSE IT DOESNT EXISTS
                            userViewModel.insert(
                                User(
                                    loggedUser.uid,
                                    loggedUser.displayName!!,
                                    loggedUser.email!!,
                                    loggedUser.photoUrl.toString(),
                                    darkTheme = isDeviceDarkTheme(),
                                    hidePaidLists = false,
                                    messagingToken = ""
                                )
                            )
                        } else {
                            //THE USER ALREADY EXISTS
                            userViewModel.updateByField(user.id, UserFieldsEnum.DARKTHEME.value, isDeviceDarkTheme())
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
    }

    private fun isDeviceDarkTheme() : Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private val activityResultLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(
                        ApiException::class.java
                    )!!
                    Log.d(className, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(className, "Google sign in failed", e)
                }
            }
        }

}