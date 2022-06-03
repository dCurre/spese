package com.dcurreli.spese.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class LiveDataUtil {
    companion object fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
        observeForever(object: Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
}