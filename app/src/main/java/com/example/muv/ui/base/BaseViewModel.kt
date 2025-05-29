package com.example.muv.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    // Loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Network error specific
    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError

    // Success/Info messages
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    /**
     * Exception handler for coroutines
     */
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    /**
     * Show loading state
     */
    protected fun showLoading() {
        _loading.value = true
    }

    /**
     * Hide loading state
     */
    protected fun hideLoading() {
        _loading.value = false
    }

    /**
     * Show error message
     */
    protected fun showError(message: String) {
        _error.value = message
    }

    /**
     * Show network error
     */
    protected fun showNetworkError() {
        _networkError.value = true
    }

    /**
     * Show success message
     */
    protected fun showMessage(message: String) {
        _message.value = message
    }

    /**
     * Handle exceptions
     */
    private fun handleException(throwable: Throwable) {
        hideLoading()
        when (throwable) {
            is java.net.UnknownHostException,
            is java.net.ConnectException -> {
                showNetworkError()
            }
            else -> {
                showError(throwable.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Launch coroutine with exception handling
     */
    protected fun launchWithExceptionHandler(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }
}