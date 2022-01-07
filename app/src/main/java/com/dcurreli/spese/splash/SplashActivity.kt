package com.dcurreli.spese.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.login.LoginActivity
import com.dcurreli.spese.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        //Se l'utente non è autenticato, verrà riportato alla pagina di login
        Handler(Looper.getMainLooper()).postDelayed({
            if(user!=null){
                val dashboardIntent = Intent(this, MainActivity::class.java)
                startActivity(dashboardIntent)
            }else{
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
            }
        }, 1000)

    }
}
