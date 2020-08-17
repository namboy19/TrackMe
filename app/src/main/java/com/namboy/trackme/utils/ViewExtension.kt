package com.namboy.trackme.utils

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.namboy.trackme.R
import java.io.File

fun ImageView.loadLocalImage(imgUrl: String) {

    Glide.with(context)
        .load(Uri.fromFile(File(imgUrl)))
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(
            RequestOptions.centerCropTransform().error(R.drawable.ic_baseline_broken_image_24)
        )
        .into(this)
}