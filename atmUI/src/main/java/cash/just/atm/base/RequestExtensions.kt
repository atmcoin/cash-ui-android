package com.square.project.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cash.just.atm.base.RequestState

fun LiveData<RequestState>.multiStateObserve(owner: LifecycleOwner, observer: (RequestState) -> Unit) {
    removeObservers(owner)
    observe(owner, Observer { state -> observer(state) })
}


fun LiveData<RequestState>.singleStateObserve(owner: LifecycleOwner, observer: (RequestState) -> Unit) {
    removeObservers(owner)
    observe(owner, Observer { state ->
        when (state) {
            is RequestState.CLEAR, null -> {
                // consume and do nothing
            }
            else -> {
                // pass the state to the observer
                observer(state)
                (this as? MutableLiveData<RequestState>)?.postValue(RequestState.CLEAR)
            }
        }
    })
}
