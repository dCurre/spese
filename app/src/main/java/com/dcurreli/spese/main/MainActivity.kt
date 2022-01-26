package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.ListaSpeseUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase




open class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var currentUser : FirebaseUser
    private val TAG = javaClass.simpleName

    @SuppressLint("ResourceType", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = DBUtils.getCurrentUser()!!
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerMainActivity)

        setupActionBarWithNavController(navController, appBarConfiguration) //Se tengo nascosto non escono i tasti

        //Header barra laterale
        binding.lateralNavViewHeader.text = "Ciao, ${(currentUser.displayName)?.split(' ')?.get(0)}"

        //Bottone settings nell'header della barra laterale
        binding.buttonSettings.setOnClickListener {
            binding.drawerMainActivity.closeDrawer(GravityCompat.START) //Chiudo il menu laterale
            navController.navigate(R.id.action_To_SettingsFragment)
        }

        //Bottom navigation bar
        binding.bottomNav.setupWithNavController(navController)

        //Stampo la lista delle spese nella barra laterale
        ListaSpeseUtils.printListe(this, binding, navController)

        //Controllo se ho un dynamic link attivo
        checkDynamicLink()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Creo il menu ma lo nascondo, l'intenzione è di usarlo solo nel load spese
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        //Nascondo il menu appena creato
        val item : MenuItem = menu.findItem(R.id.share)
        item.isVisible = false

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

    private fun checkDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                if (deepLink != null) {
                    //Navigo sul fragment successivo passandogli il bundle con id lista
                    navController.navigate(R.id.joinFragment, GenericUtils.createBundleForListaSpese(deepLink.getQueryParameter("group")!!, null))

                } else {
                    Log.d(ContentValues.TAG, "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.w(ContentValues.TAG, "getDynamicLink:onFailure", e) }
    }
}