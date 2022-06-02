package com.dcurreli.spese.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ActivitySplashBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.view.MainActivity
import com.dcurreli.spese.view.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private lateinit var binding: ActivitySplashBinding
    private val className = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toLogUser = DBUtils.getToLogUser()
        binding = ActivitySplashBinding.inflate(layoutInflater)

        DBUtils.clearFirestorePersistence()

        //Fase di login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent

            if(toLogUser == null){
                //Se non sono loggato, vengo riportato alla pagina di Login
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return@postDelayed
            }

            //Se è tutto ok, vado alla main activity
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }, 2000)

    }
}
