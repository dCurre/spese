package com.dcurreli.spese.utils

import com.dcurreli.spese.enums.table.TablesEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DBUtils {
    private val className = javaClass.simpleName
    private var mAuth= FirebaseAuth.getInstance()
    private val dbReference = Firebase.database.reference

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

    fun getDatabaseReference(table : TablesEnum): DatabaseReference {
        return dbReference.child(table.value)
    }

    fun getDatabaseReferenceFirestore(table : TablesEnum): CollectionReference {
        return Firebase.firestore.collection(table.value)
    }

    fun clearFirestorePersistence(){
        Firebase.firestore.clearPersistence()
    }

}