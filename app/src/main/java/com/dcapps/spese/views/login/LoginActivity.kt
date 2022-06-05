package com.dcapps.spese.views.login

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private val className = javaClass.simpleName
    private lateinit var binding : ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth
    private lateinit var userViewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        mAuth = DBUtils.getAuthentication()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()
    }

    private fun signIn() {
        binding.signInBtn.setOnClickListener {
            Log.d(className, ">> signIn()")
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
            Log.d(className, "<< signIn()")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(className, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(className, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //DOPO LA LOGIN CREO L'UTENTE SE NON ESITE
                    val loggedUser = DBUtils.getLoggedUser()

                    userViewModel.findByID(loggedUser.uid)
                    userViewModel.userLiveData.observe(this) { user ->
                        if (user == null) {
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
                            userViewModel.updateByField(user.id, UserFieldsEnum.DARKTHEME.value, isDeviceDarkTheme())
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private fun isDeviceDarkTheme() : Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}