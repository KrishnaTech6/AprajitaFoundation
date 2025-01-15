package com.example.aprajitafoundation.api

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The SingleLiveEvent class ensures that the event is consumed only once by using an AtomicBoolean flag (mPending).
 * It overrides the observe() and setValue() methods to add logic that prevents multiple emissions of the same event to the observer.
 * The call() method is a convenience method to trigger a "one-time" event, and postValue(null) is used to set the value to null, effectively notifying the observer that an event has occurred without providing specific data.*/
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)//to track whether an event has been consumed or not

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
