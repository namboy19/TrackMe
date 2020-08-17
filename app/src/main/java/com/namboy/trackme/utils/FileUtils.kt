package com.namboy.trackme.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*


object FileUtils {

    fun saveBitmap(context: Context,bitmap: Bitmap, callback:(String?)->Unit){
        val cw = ContextWrapper(context)

        val directory = cw.getDir("history", Context.MODE_PRIVATE)
        if (!directory.exists()) {
            directory.mkdir()
        }
        val mypath = File(directory, "${Calendar.getInstance().timeInMillis}.png")

        var fos: FileOutputStream
        try {
            fos = FileOutputStream(mypath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            callback.invoke(mypath.absolutePath)
        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", e.message, e)
            callback.invoke(null)
        }
    }

}