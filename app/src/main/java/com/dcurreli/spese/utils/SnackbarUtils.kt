package com.dcurreli.spese.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import com.dcurreli.spese.R
import com.google.android.material.snackbar.Snackbar


object SnackbarUtils {

    fun showSnackbarError(message: String, constraintLayout: ConstraintLayout) {
        val snackbar = Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(errorColor)
        snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    fun showSnackbarOK(message: String, constraintLayout: ConstraintLayout) {
        val snackbar = Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(successColor)
        snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    fun showSnackbarOpenPath(message: String, constraintLayout: ConstraintLayout, pathToOpen: String, context: Context) {
        val snackbar = Snackbar
            .make(constraintLayout, message, Snackbar.LENGTH_LONG)
            //.setAnchorView(R.id.bottom_nav)
            .setAction("APRI"){
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(pathToOpen), "*/*")
                startActivity(context, intent, null)
            }
            .setActionTextColor(Color.WHITE)

        snackbar.view.setBackgroundColor(successColor)
        snackbar.show()
    }

    private val className = javaClass.simpleName
    private val successColor = Color.rgb(0, 128, 0)
    private val errorColor = Color.rgb(180, 0, 0)

}
