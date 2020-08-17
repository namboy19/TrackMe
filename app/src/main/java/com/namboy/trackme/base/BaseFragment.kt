package com.namboy.trackme.base

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

open class BaseFragment : Fragment() {

    open var tagName: String? = javaClass.name
    var baseActivity: BaseLifecycleActivity<*>? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseLifecycleActivity<*>) {
            baseActivity = context
        } else {
            Log.e("onAttach", "ActivityItem not instanceof BaseActivity")
        }
    }

    fun goBack() {
        (activity as? BaseActivity)?.backStack?.popFragment()
    }

    fun showLoading(isShow: Boolean) {
        (activity as? BaseActivity)?.showLoading(isShow)
    }
}