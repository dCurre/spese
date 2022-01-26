package com.dcurreli.spese.main.splash

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ActivitySplashBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.main.login.LoginActivity
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
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = DBUtils.getCurrentUser()
        binding = ActivitySplashBinding.inflate(layoutInflater)

        //Fase di login
        Handler(Looper.getMainLooper()).postDelayed({
            var intent: Intent
            if(user!=null){
                this.db.child(user.uid).get().addOnSuccessListener {
                    //Se trovo l'utente carico le sue impostazioni
                    if (it.exists()) {
                        utente = it.getValue(Utente::class.java) as Utente
                        GenericUtils.onOffDarkTheme(db, user, utente.isDarkTheme) // Gestisco preferenze tema
                   }
                }.addOnFailureListener {
                    Log.e(TAG, "<<Error getting utente", it)
                }

                //Se è tutto ok, vado alla main activity
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else{
                //Se non sono loggato, vengo riportato alla pagina di login
                intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            startActivity(intent)
            finish()
        }, 2000)

    }
}
