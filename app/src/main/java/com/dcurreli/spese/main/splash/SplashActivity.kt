package com.dcurreli.spese.main.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.databinding.ActivitySplashBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.main.login.LoginActivity
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private var db = DBUtils.getDatabaseReference(TablesEnum.UTENTE)
    private lateinit var user : User
    private lateinit var binding: ActivitySplashBinding
    private val className = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = DBUtils.getCurrentUser()
        binding = ActivitySplashBinding.inflate(layoutInflater)

        //Fase di login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent
            if(user!=null){
                //TODO gestire in tutto il progetto in caso di connessione mancante
                this.db.child(user.uid).get().addOnSuccessListener {
                    //Se trovo l'user carico le sue impostazioni
                    if (it.exists()) {
                        this.user = it.getValue(User::class.java) as User
                        GenericUtils.onOffDarkTheme(db, user, this.user.isDarkTheme) // Gestisco preferenze tema
                        GenericUtils.onOffNascondiListe(db, user, this.user.isNascondiListeSaldate) // Gestisco preferenze liste saldate nascoste
                    }
                }.addOnFailureListener {
                    Log.e(className, "<<Error getting user", it)
                }

                //Se è tutto ok, vado alla main activity
                intent = Intent(this, MainActivity::class.java)
            }else{
                //Se non sono loggato, vengo riportato alla pagina di login
                intent = Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 2000)

    }
}
