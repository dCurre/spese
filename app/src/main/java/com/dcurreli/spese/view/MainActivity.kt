package com.dcurreli.spese.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.enums.bundle.DeepLinkEnum
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var expensesListViewModel : ExpensesListViewModel
    private val className = javaClass.simpleName
    private val requestExternalStorage = 1
    private val permissionStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration) //Se tengo nascosto non escono i tasti

        //Bottom navigation bar
        binding.bottomNav.setupWithNavController(navController)

        //Controllo se ho un dynamic link attivo
        checkDynamicLink()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Creo il menu ma lo nascondo, l'intenzione è di usarlo solo nel load spese
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        //Nascondo il menu appena creato
        menu.findItem(R.id.share).isVisible = false
        menu.findItem(R.id.edit).isVisible = false

        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkDynamicLink()
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
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri? = pendingDynamicLinkData?.link

                if (deepLink != null) {
                    val expensesListID = deepLink.getQueryParameter(DeepLinkEnum.LIST.value)!!
                    expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
                    expensesListViewModel.findByID(expensesListID)
                    expensesListViewModel.expensesListLiveData.observeOnce { expensesList ->
                        if (expensesList != null && expensesList.partecipatingUsersID?.contains(
                                DBUtils.getLoggedUser().uid
                            ) == true
                        ) {
                            navController.navigate(
                                R.id.loadSpeseFragment,
                                GenericUtils.createBundleForListaSpese(
                                    expensesListID,
                                    expensesList.name,
                                    expensesList.owner
                                )
                            )
                        } else {
                            //Navigo sul fragment successivo passandogli il bundle con id lista
                            navController.navigate(
                                R.id.joinFragment,
                                GenericUtils.createBundleForListaSpese(
                                    expensesListID,
                                    null,
                                    null
                                )
                            )
                        }
                    }
                }
            }
    }

    fun setBottomNavVisibility(bool : Boolean){
        binding.bottomNav.isVisible = bool
    }

    fun setToolbarTitle(string: String?){
        binding.toolbar.title = string
    }

    fun checkUserPermission() {
        //Se non ci sono i permessi di scrittura --> informo l'utente
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionStorage, requestExternalStorage)
        }
    }

    private fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object: Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
}