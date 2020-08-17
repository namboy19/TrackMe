package com.namboy.trackme.base

class InlineCallback<T>(val arg: T? = null, var callback: ((T?) -> Unit)?) {
    fun invoke() {
        callback?.invoke(arg)
    }
}