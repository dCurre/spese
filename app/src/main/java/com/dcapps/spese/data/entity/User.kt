package com.dcapps.spese.data.entity

import android.util.Log
import com.dcapps.spese.enums.entity.UserFieldEnum
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
                    getString(UserFieldEnum.FULLNAME.value)!!,
                    getString(UserFieldEnum.EMAIL.value)!!,
                    getString(UserFieldEnum.PROFILE_IMAGE.value)!!,
                    getBoolean(UserFieldEnum.DARKTHEME.value)!!,
                    getBoolean(UserFieldEnum.HIDE_PAID_LISTS.value)!!,
                    get(UserFieldEnum.MESSAGING_TOKEN_LIST.value) as ArrayList<String>,
                )

            } catch (e: Exception) {
                Log.i("$TAG.toUser()", "${e.message}")

                null
            }
        }

        private const val TAG = "User"
    }

}