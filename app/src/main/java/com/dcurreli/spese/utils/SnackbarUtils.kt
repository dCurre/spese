package com.dcurreli.spese.utils

import android.annotation.SuppressLint
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
        //snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    fun showSnackbarOK(message: String, constraintLayout: ConstraintLayout) {
        val snackbar = Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(successColor)
        //snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    fun showSnackbarOKOverBottomnav(message: String, constraintLayout: ConstraintLayout) {
        val snackbar = Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(successColor)
        snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun showSnackbarOpenPath(message: String, constraintLayout: ConstraintLayout, pathToOpen: String, context: Context) {

        val snackbar = Snackbar
            .make(constraintLayout, message, Snackbar.LENGTH_LONG)
            //.setAnchorView(R.id.bottom_nav)
            .setAction("APRI"){
                val intent = Intent(Intent.ACTION_VIEW)

                intent.setDataAndType(
                    Uri.parse(pathToOpen),
                    when {//Setto il tipo di applicazione da aprire
                        // Word document
                        pathToOpen.endsWith(".doc") || pathToOpen.endsWith(".docx") -> { "application/msword" }
                        // PDF file
                        pathToOpen.endsWith(".pdf") -> { "application/pdf" }
                        // Powerpoint file
                        pathToOpen.endsWith(".ppt") || pathToOpen.endsWith(".pptx") -> { "application/vnd.ms-powerpoint" }
                        // Excel file
                        pathToOpen.endsWith(".xls") || pathToOpen.endsWith(".xlsx") -> { "application/wps-office.xltx" }
                        // ZIP audio file
                        pathToOpen.endsWith(".zip") || pathToOpen.endsWith(".rar") -> { "application/zip" }
                        // RTF file
                        pathToOpen.endsWith(".rtf") -> { "application/rtf" }
                        // WAV audio file
                        pathToOpen.endsWith(".wav") || pathToOpen.endsWith(".mp3") -> { "audio/x-wav" }
                        // GIF file
                        pathToOpen.endsWith(".gif") -> { "image/gif" }
                        // JPG file
                        pathToOpen.endsWith(".jpg") || pathToOpen.endsWith(".jpeg") || pathToOpen.endsWith(".png") -> { "image/jpeg" }
                        // Text file
                        pathToOpen.endsWith(".txt") -> { "text/plain" }
                        // Video files
                        pathToOpen.endsWith(".3gp") || pathToOpen.endsWith(".mpg") || pathToOpen.endsWith(".mpeg") || pathToOpen.endsWith(".mpe") || pathToOpen.endsWith(".mp4") || pathToOpen.endsWith(".avi") -> { "video/*" }
                        //if you want you can also define the intent type for any other file
                        //additionally use else clause below, to manage other unknown extensions
                        //in this case, Android will show all applications installed on the device
                        //so you can choose which application to use
                        else -> { "*/*" }
                    }
                )
                if (intent.resolveActivity(context.packageManager) != null)
                {
                    startActivity(context, intent, null)
                } else {
                    val pathArray = pathToOpen.split(".")
                    showSnackbarError("Nessun app installata per poter aprire il file in formato .${pathArray[pathArray.size-1]}", constraintLayout)
                }
            }
            .setActionTextColor(Color.WHITE)

        snackbar.view.setBackgroundColor(successColor)
        snackbar.show()
    }

    private val className = javaClass.simpleName
    private val successColor = Color.rgb(0, 128, 0)
    private val errorColor = Color.rgb(180, 0, 0)

}
