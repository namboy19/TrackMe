package com.namboy.trackme.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.namboy.trackme.R


object DialogFactory {

    fun createSimpleOkDialog(context: Context, title: String, message: String, buttonText: String): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { dialog, which ->
            }

        return builder.create()
    }

}