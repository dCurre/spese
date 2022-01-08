package com.dcurreli.spese.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.login.LoginActivity
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.utils.DBUtils

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = DBUtils.getCurrentUser()

        //Se l'utente non è autenticato, verrà riportato alla pagina di login
        Handler(Looper.getMainLooper()).postDelayed({
            if(user!=null){
                val dashboardIntent = Intent(this, MainActivity::class.java)
                startActivity(dashboardIntent)
                finish()
            }else{
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        }, 1000)

    }
}
