package com.dcurreli.spese.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.data.viewmodel.ExpenseViewModel
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.view.HomeFragment


object SpesaUtils {
    private val className = javaClass.simpleName
    val dbSpesa = DBUtils.getDatabaseReferenceFirestore(TablesEnum.EXPENSE)
    val dbListe = DBUtils.getDatabaseReferenceFirestore(TablesEnum.EXPENSES_LISTS)

    //TODO DA CANCELLARE LA CLASSE

    fun exportDataToFirestore(homeFragment: HomeFragment, viewLifecycleOwner: LifecycleOwner) {
        val listaSpeseModel = ViewModelProvider(homeFragment)[ExpensesListViewModel::class.java]
        val spesaModel = ViewModelProvider(homeFragment)[ExpenseViewModel::class.java]

        //listaSpeseModel.findAll()
        //spesaModel.findAll()
/**
        listaSpeseModel.listaSpeseListLiveData.observe(viewLifecycleOwner) { list ->
            for(element in list){
                dbListe.document(element.id).set(
                    ExpensesList(
                        element.id,
                        element.nome,
                        element.partecipanti,
                        element.owner,
                        element.isSaldato,
                        element.timestamp
                    )
                )
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