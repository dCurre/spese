package com.dcapps.spese.views.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dcapps.spese.R
import com.dcapps.spese.data.viewmodels.UserViewModel
import com.dcapps.spese.databinding.ActivitySplashBinding
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.views.MainActivity
import com.dcapps.spese.views.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var userViewModel : UserViewModel
    private val className = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toLogUser = DBUtils.getToLogUser()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        //Nel caso rimanga qualcosa in cache la elimino (vedere se può creare casini)
        DBUtils.clearFirestorePersistence()

        if(toLogUser == null){
            //Se non sono loggato, vengo riportato alla pagina di Login
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        userViewModel.findByID(toLogUser.uid)
        userViewModel.userLiveData.observeOnce { user ->
            GenericUtils.onOffDarkTheme(user.darkTheme)
        }

        //Fase di login
        Handler(Looper.getMainLooper()).postDelayed({
            //Is the user exists
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
    private fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
}
