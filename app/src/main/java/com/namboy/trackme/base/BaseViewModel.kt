package com.namboy.trackme.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(application: Application) : AndroidViewModel(application) , CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + CoroutineExceptionHandler(handler) + SupervisorJob()

    private val handler: ((CoroutineContext, Throwable) -> Unit) = { coroutineContext, throwable ->
        isLoadingLiveData.postValue(false)
        throwable.printStackTrace()
        errorLiveData.postValue(throwable.message)
    }

    var isLoadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()
    val inlineCallback = MutableLiveData<InlineCallback<*>>()

}