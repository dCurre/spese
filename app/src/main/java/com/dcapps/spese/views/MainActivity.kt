package com.dcapps.spese.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.dcapps.spese.R
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.data.viewmodels.UserViewModel
import com.dcapps.spese.databinding.ActivityMainBinding
import com.dcapps.spese.enums.entity.UserFieldsEnum
import com.dcapps.spese.enums.firebase.deeplink.DeepLinkParametersEnum
import com.dcapps.spese.utils.DBUtils
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.utils.SnackbarUtils
import com.dcapps.spese.views.dialog.EditSpesaDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var expensesListViewModel : ExpensesListViewModel
    private lateinit var userViewModel : UserViewModel
    private val requestExternalStorage = 1
    private val permissionStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration) //Se tengo nascosto non escono i tasti

        //Bottom navigation bar
        binding.bottomNav.setupWithNavController(navController)

        //Message token set or update
        manageMessagingToken()

        //Controllo se ho un dynamic link attivo
        checkDynamicLink()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Creo il menu ma lo nascondo, l'intenzione ?? di usarlo solo nel load spese
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        //Nascondo il menu appena creato
        menu.findItem(R.id.share).isVisible = false
        menu.findItem(R.id.list_settings).isVisible = false

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
        Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener(this) { pendingDynamicLinkData ->
            // Get deep link from result (may be null if no link is found) --> if null returns
            val deepLink: Uri = pendingDynamicLinkData?.link ?: return@addOnSuccessListener

            val expensesListID = deepLink.getQueryParameter(DeepLinkParametersEnum.LIST.value)!!
            expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
            expensesListViewModel.findByID(expensesListID)
            expensesListViewModel.expensesListLiveData.observeOnce { expensesList ->

                //If the list doesnt exists anymore
                if(expensesList == null) {
                    Log.e("CHECK", "A QUANTO PARE LA LISTA E' NULLA")
                    SnackbarUtils.showSnackbarErrorOverBottomnav("La lista non esiste pi??", binding.root)
                    return@observeOnce
                }

                //If the user is already partecipanting in the list --> redirection to the list fragment else join fragment
                navController.navigate(
                    if(expensesList.partecipants?.contains(DBUtils.getLoggedUser().uid) == true) R.id.loadSpeseFragment else R.id.joinFragment,
                    GenericUtils.createBundleForListaSpese(
                        expensesListID,
                        expensesList.name,
                        expensesList.owner
                    )
                )
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

    private fun manageMessagingToken() {
        userViewModel.findByID(DBUtils.getLoggedUser().uid)
        userViewModel.userLiveData.observeOnce { user ->

            //Update token
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(EditSpesaDialogFragment.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                if(task.result.equals(user.messagingToken)){
                    Log.e(EditSpesaDialogFragment.TAG, "Message token already stored")
                    return@OnCompleteListener
                }

                //If this point is reached I need to resubscribe every list in which the user partecipates
                expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
                expensesListViewModel.findAllByUserID(user.id)
                expensesListViewModel.expensesListsLiveData.observeOnce { expensesLists ->
                    expensesLists.forEach { expensesList -> Firebase.messaging.subscribeToTopic(expensesList.id!!) }
                }

                //And then store the new token for future comparisons
                userViewModel.updateByField(user.id, UserFieldsEnum.MESSAGING_TOKEN.value, task.result)
            })
        }
    }
}