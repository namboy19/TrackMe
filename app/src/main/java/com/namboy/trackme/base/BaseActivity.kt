package com.namboy.trackme.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.namboy.trackme.R
import kotlinx.android.synthetic.main.widget_loading_view.*


abstract class BaseActivity : AppCompatActivity() {

    val backStack: FragmentBackStack by lazy {
        FragmentBackStack(supportFragmentManager, getLayoutContentId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(getResourceLayout())
    }

    open fun getResourceLayout() = R.layout.activity_default
    open fun getLayoutContentId() = R.id.frame_content


    fun showLoading(isShow:Boolean){
        loading_view?.visibility=if (isShow) View.VISIBLE else View.GONE
    }

}