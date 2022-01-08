package com.dcurreli.spese.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object DBUtils {
    private val TAG = javaClass.simpleName
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    fun getAuthentication(): FirebaseAuth {
        return this.mAuth
    }

}