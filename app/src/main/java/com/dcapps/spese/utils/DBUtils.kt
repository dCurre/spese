package com.dcapps.spese.utils

import com.dcapps.spese.enums.table.TablesEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DBUtils {
    private var mAuth= FirebaseAuth.getInstance()

    fun getAuthentication(): FirebaseAuth {
        return this.mAuth
    }

    fun getLoggedUser(): FirebaseUser {
        //L'utente è per forza loggato
        return getAuthentication().currentUser!!
    }

    fun getToLogUser(): FirebaseUser? {
        return getAuthentication().currentUser
    }

    fun getFirestoreReference(table : TablesEnum): CollectionReference {
        return Firebase.firestore.collection(table.value)
    }

    fun clearFirestorePersistence(){
        Firebase.firestore.clearPersistence()
    }

}