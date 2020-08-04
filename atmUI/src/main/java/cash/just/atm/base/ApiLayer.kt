package cash.just.atm.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val executor: ExecutorService by lazy { Executors.newFixedThreadPool(8) }

fun ViewModel.execute(errorHandler: (exception: Exception) -> Unit, successHandler: () -> Unit) {
    executor.execute {
        try {
            successHandler()
        } catch (e: Exception) {
            errorHandler(e)
        }
    }
}

fun ViewModel.executeWithoutLoading(state: MutableLiveData<RequestState>, successHandler: () -> Any?) {
    executor.execute {
        try {
            val result = successHandler()
            if (result != null) {
                state.postValue(RequestState.success(result))
            }
        } catch (e: Throwable) {
            // todo: Add a message lookup?
            state.postValue(RequestState.error(e))
        }
    }
}

fun ViewModel.execute(state: MutableLiveData<RequestState>, successHandler: () -> Any?) {
    state.postValue(RequestState.LOADING)
    executor.execute {
        try {
            val result = successHandler()
            if (result != null) {
                state.postValue(RequestState.success(result))
            }
        } catch (e: Throwable) {
            // todo: Add a message lookup?
            state.postValue(RequestState.error(e))
        }
    }
}