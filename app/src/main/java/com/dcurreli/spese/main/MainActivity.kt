package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.MeseUtils
import com.google.firebase.auth.FirebaseUser

open class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var currentUser : FirebaseUser
    private val TAG = javaClass.simpleName

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = DBUtils.getCurrentUser()!!
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerMainActivity)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //Stampo la lista mesi
        MeseUtils.printMese(this, binding, navController)

        //Abilita il menu inferiore
        binding.bottomNav.setupWithNavController(navController)

        binding.lateralNavViewHeader.text = "Ciao, ${(currentUser.displayName)?.split(' ')?.get(0)}"
        //Glide.with(this).load(currentUser?.photoUrl).into(binding.profileImage)
        //id_txt.text = currentUser?.uid
        //email_txt.text = currentUser?.email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_laterale, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}