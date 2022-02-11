package com.dcurreli.spese.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DBUtils {
    private val className = javaClass.simpleName
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: DatabaseReference = Firebase.database.reference

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    fun getAuthentication(): FirebaseAuth {
        return this.mAuth
    }

}