package com.dcurreli.spese.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.data.viewmodel.SpesaViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.view.HomeFragment


object SpesaUtils {
    private val className = javaClass.simpleName
    val dbUtenti = DBUtils.getDatabaseReferenceFirestore(TablesEnum.USER)
    val dbSpesa = DBUtils.getDatabaseReferenceFirestore(TablesEnum.SPESA_FIRESTORE)
    val dbListe = DBUtils.getDatabaseReferenceFirestore(TablesEnum.LISTE_FIRESTORE)

    //TODO DA CANCELLARE LA CLASSE

    fun exportDataToFirestore(homeFragment: HomeFragment, viewLifecycleOwner: LifecycleOwner) {
        val userModel = ViewModelProvider(homeFragment)[UserViewModel::class.java]
        val listaSpeseModel = ViewModelProvider(homeFragment)[ListaSpeseViewModel::class.java]
        val spesaModel = ViewModelProvider(homeFragment)[SpesaViewModel::class.java]

        //userModel.getAll()
        //listaSpeseModel.findAll()
        //spesaModel.findAll()
/*
        userModel.userListLiveData.observe(viewLifecycleOwner) { userList ->
            for(user in userList){
                dbUtenti.document(user.user_id).set(UserFirestore(user.user_id, user.nominativo, user.email, user.image, user.isDarkTheme, user.isNascondiListeSaldate))
            }
        }
*/
    }

/*

    @RequiresApi(Build.VERSION_CODES.O)
    fun fireStoreCreaSpesa(binding: AddSpesaBinding, idLista : String) {
        val methodName = "creaSpesa"
        Log.i(className, ">>$methodName")

        val newKey = db.document().id

        //Nuova spesa
        val spesa = Spesa(
            newKey,
            binding.spesaSpesaText.text.toString().trim(),
            binding.spesaImporto.text.toString().trim().replace(",", ".").toDouble(),
            binding.spesaData.text.toString().trim(),
            binding.spesaPagatoreText.text.toString().trim(),
            idLista
        )

        db.document(newKey).set(spesa)

        Log.i(className, "<<$methodName")
    }
*/
}