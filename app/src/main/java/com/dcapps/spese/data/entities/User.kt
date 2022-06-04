package com.dcapps.spese.data.entities

import android.util.Log
import com.dcapps.spese.enums.entity.UserFieldsEnum
import com.google.firebase.firestore.DocumentSnapshot

data class User (
    val id : String,
    val fullname : String,
    val email : String,
    val profileImage : String,
    val darkTheme : Boolean,
    val hidePaidLists : Boolean,
    val messagingTokenList : ArrayList<String>?
        ) {

    companion object {
        fun DocumentSnapshot.toUser(): User? {

            return try {
                User(
                    id,
                    getString(UserFieldsEnum.FULLNAME.value)!!,
                    getString(UserFieldsEnum.EMAIL.value)!!,
                    getString(UserFieldsEnum.PROFILE_IMAGE.value)!!,
                    getBoolean(UserFieldsEnum.DARKTHEME.value)!!,
                    getBoolean(UserFieldsEnum.HIDE_PAID_LISTS.value)!!,
                    get(UserFieldsEnum.MESSAGING_TOKEN_LIST.value) as ArrayList<String>,
                )

            } catch (e: Exception) {
                Log.i("$TAG.toUser()", "${e.message}")

                null
            }
        }

        private const val TAG = "User"
    }

}