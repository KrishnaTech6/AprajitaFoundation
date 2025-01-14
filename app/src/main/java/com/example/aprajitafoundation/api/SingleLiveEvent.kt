package com.example.aprajitafoundation.api

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Only emit event if it hasn't been consumed
        super.observe(owner, Observer { t ->
            if (mPending.compareAndSet(false, true)) {
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(value: T) {
        mPending.set(false)
        super.setValue(value)
    }

    fun call() {
        postValue(null)
    }
}
