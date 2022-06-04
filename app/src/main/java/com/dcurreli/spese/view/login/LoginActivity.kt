package com.dcurreli.spese.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.ActivityLoginBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.view.MainActivity
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
    private lateinit var userModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
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

                    userModel.findByID(loggedUser.uid)
                    userModel.userLiveData.observe(this) { user ->
                        if (user == null) {
                            userModel.insert(
                                User(
                                    loggedUser.uid,
                                    loggedUser.displayName!!,
                                    loggedUser.email!!,
                                    loggedUser.photoUrl.toString(),
                                    darkTheme = false,
                                    hidePaidLists = false,
                                    ArrayList<String>()
                                )
                            )
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
}