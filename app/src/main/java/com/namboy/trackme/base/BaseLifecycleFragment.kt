package com.namboy.trackme.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

abstract class BaseLifecycleFragment<T : BaseViewModel> : BaseFragment() {

    abstract val viewModelClass: Class<T>
    val viewModel: T by lazy { initViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden)
            viewModel.isLoadingLiveData.postValue(false)
    }

    open fun observeLiveData() {
        viewModel.inlineCallback.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.invoke()
                viewModel.inlineCallback.value = null
            }
        })
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            it?.let {
                onChangedLoadingStatus(it)
            }
        })
        viewModel.errorLiveData.observe(viewLifecycleOwner, Observer<String> {
            it?.let {
                onError(it)
                viewModel.errorLiveData.value = null
            }
        })
    }


    open fun initViewModel() = ViewModelProvider(this).get(viewModelClass)

    open fun onChangedLoadingStatus(isShowLoader: Boolean) {

    }

    open fun onError(errorResponse: String) {
        baseActivity?.onError(errorResponse)
    }

}