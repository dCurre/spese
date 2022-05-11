package com.dcurreli.spese.main.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ActivityLoginBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.objects.Utente
import com.dcurreli.spese.utils.DBUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var binding : ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth
    private val className = javaClass.simpleName
    private var db: DatabaseReference = DBUtils.getDatabaseReference(TablesEnum.UTENTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = DBUtils.getAuthentication()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        Log.d(className, ">> signIn()")
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        Log.d(className, "<< signIn()")
    }

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
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential: success")

                    //DOPO LA LOGIN CREO L'UTENTE SE NON ESITE
                    val user = DBUtils.getCurrentUser()
                    if (user != null) {
                        this.db.child(user.uid).get().addOnSuccessListener {
                            //Se non esiste creo l'utente nella lista utenti
                            if (!it.exists()) {
                                val uid = Utente(user.uid,user.displayName, user.email, user.photoUrl.toString())
                                db.child(user.uid).setValue(uid)
                            }
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }else {
                    // If sign in fails, display a message to the user.
                    Log.d("SignInActivity", "signInWithCredential:failure")
                }
            }
    }
}