package com.example.muv.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.muv.R
import com.example.muv.data.model.Resource
import com.example.muv.ui.components.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    // Abstract properties that must be implemented by child activities
    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    // DataBinding and ViewModel instances
    protected lateinit var binding: DB
    protected lateinit var viewModel: VM

    // Loading dialog or progress indicator
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DataBinding
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[viewModelClass]

        // Setup common observers
        setupBaseObservers()

        // Setup toolbar if needed
        setupToolbar()

        // Initialize child-specific setup
        initView()
        initData()
        initListener()
    }

    /**
     * Setup common observers for all activities
     */
    private fun setupBaseObservers() {
        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            handleLoadingState(isLoading)
        }

        // Observe error messages
        viewModel.error.observe(this) { errorMessage ->
            handleError(errorMessage)
        }

        // Observe network status
        viewModel.networkError.observe(this) { isNetworkError ->
            if (isNetworkError) {
                showNetworkError()
            }
        }

        // Observe success messages
        viewModel.message.observe(this) { message ->
            showMessage(message)
        }
    }

    /**
     * Setup toolbar with common configurations
     */
    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    /**
     * Handle loading state - show/hide loading indicator
     */
    protected open fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    /**
     * Show loading dialog
     */
    private fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
        }
        loadingDialog?.show(supportFragmentManager, "LoadingDialog")
    }

    /**
     * Hide loading dialog
     */
    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

    /**
     * Handle error messages
     */
    protected open fun handleError(errorMessage: String?) {
        errorMessage?.let {
            showErrorSnackbar(it)
        }
    }

    /**
     * Show error message using Snackbar
     */
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("retry") {
                onRetryClicked()
            }
            show()
        }
    }

    /**
     * Show network error with specific handling
     */
    private fun showNetworkError() {
        if (!isNetworkAvailable()) {
            showErrorSnackbar("error_network")
        }
    }

    /**
     * Show success/info messages
     */
    protected fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Check network availability
     */
    protected fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    /**
     * Handle Resource state from Repository
     */
    protected fun <T> handleResource(resource: Resource<T>, onSuccess: (T) -> Unit) {
        when (resource) {
            is Resource.Loading -> {
                handleLoadingState(true)
            }
            is Resource.Success -> {
                handleLoadingState(false)
                resource.data?.let { onSuccess(it) }
            }
            is Resource.Error -> {
                handleLoadingState(false)
                handleError(resource.message)
            }
        }
    }

    /**
     * Handle back navigation
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Called when retry button is clicked
     */
    protected open fun onRetryClicked() {
        // Override in child activities if needed
    }

    // Abstract methods that must be implemented by child activities

    /**
     * Initialize views and setup UI components
     */
    abstract fun initView()

    /**
     * Initialize data and setup observers
     */
    abstract fun initData()

    /**
     * Setup click listeners and other interactions
     */
    abstract fun initListener()

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.dismiss()
    }
}