package com.dcurreli.spese.splash

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.login.LoginActivity
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.objects.Utente
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.UTENTE.value.lowercase())
    private lateinit var utente : Utente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = DBUtils.getCurrentUser()

        //Se l'utente non è autenticato, verrà riportato alla pagina di login
        Handler(Looper.getMainLooper()).postDelayed({
            if(user!=null){
                db.child(user.uid).get().addOnSuccessListener {
                    //Se non esiste creo l'utente nella lista utenti
                    if (!it.exists()) {
                        val uid = Utente(user.uid)
                        db.child(user.uid).setValue(uid)
                    }else{
                        //Altrimenti carico le impostazioni dell'utente
                        utente = it.getValue(Utente::class.java) as Utente
                        GenericUtils.onOffDarkTheme(db, user, utente.isDarkTheme) // Gestisco preferenze tema
                    }
                }.addOnFailureListener {
                    Log.e(TAG, "<<Error getting utente", it)
                }

                //Se è tutto ok, vado alla main activity
                val dashboardIntent = Intent(this, MainActivity::class.java)
                startActivity(dashboardIntent)
                finish()
            }else{
                //Se non sono loggato, vengo riportato alla pagina di login
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        }, 2000)

    }
}
