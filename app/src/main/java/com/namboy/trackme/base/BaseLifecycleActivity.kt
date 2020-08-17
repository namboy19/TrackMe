package com.namboy.trackme.base

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.namboy.trackme.R
import com.namboy.trackme.utils.DialogFactory

abstract class BaseLifecycleActivity<T : BaseViewModel> : BaseActivity() {
    abstract val viewModelClass: Class<T>
    protected val viewModel: T by lazy { ViewModelProvider(this).get(viewModelClass) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
    }


    open fun observeLiveData() {
        viewModel.inlineCallback.observe(this, Observer {
            it?.let {
                it.invoke()
                viewModel.inlineCallback.value = null
            }
        })
        viewModel.isLoadingLiveData.observe(this, Observer<Boolean> {
            it?.let {
                onChangedLoadingStatus(it)
            }
        })

        viewModel.errorLiveData.observe(this, Observer<String> {
            it?.let {
                onError(it)
            }
        })
    }

    open fun onChangedLoadingStatus(isShowLoader: Boolean) {
    }

    fun onError(error: String) {
        showAlert(error)
    }


    fun showAlert(error: String) {
        if (isFinishing || isDestroyed)
            return
        DialogFactory.createSimpleOkDialog(this, getString(R.string.alert), error, getString(R.string.ok)).show()
    }
}