package com.dcurreli.spese.utils

import com.dcurreli.spese.enum.TablesEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DBUtils {
    private val className = javaClass.simpleName
    private var mAuth= FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    fun getAuthentication(): FirebaseAuth {
        return this.mAuth
    }

    fun getDatabaseReference(table : TablesEnum): DatabaseReference {
        return Firebase.database.reference.child(table.value)
    }

    fun getDatabaseReference(table : String): DatabaseReference {
        return Firebase.database.reference.child(table)
    }

}