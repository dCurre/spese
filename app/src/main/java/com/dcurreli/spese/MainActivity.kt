package com.dcurreli.spese

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController
    private var dataItem : String = ""
    private lateinit var dataForQuery : DataForQuery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //Attiva onNavigationItemSelected (LASCIARE) --> gestisce gli onclick della barra laterale
        binding.lateralNavView.setNavigationItemSelectedListener (this)

        //Abilita il menu inferiore
        binding.bottomNav.setupWithNavController(navController)

        //Abilita il menu laterale
        //binding.lateralNavView.setupWithNavController(navController)

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


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var bool : Boolean = true;

        when(item.itemId){
            R.id.homeFragment -> { bool = true }
            R.id.loadSpeseFragment -> {
                dataItem = "Dicembre 2021"
                bool = true
            }
            else -> true
        }
        var pattern : String = "yyyy-MM-dd" //Pattern mezzo inutile

        //Passo le date per la query a LoadSpeseFragment
        dataForQuery = DataForQuery(
            GenericUtils.dateStringToTimestampSeconds(
                GenericUtils.firstDayOfMonth(dataItem),
                pattern
            ).toDouble(),
            GenericUtils.dateStringToTimestampSeconds(
                GenericUtils.lastDayOfMonth(dataItem),
                pattern).toDouble()
        )

        navController.navigate(R.id.loadSpeseFragment)
        var drawerLayout : DrawerLayout = findViewById(R.id.drawerMainActivity)
        binding.drawerMainActivity.closeDrawer(GravityCompat.START)

        return bool;

    }

    fun getDataForQuery(): DataForQuery = dataForQuery
}