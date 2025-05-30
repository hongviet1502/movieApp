package com.example.muv.ui.base

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.muv.R
import com.example.muv.data.model.Resource
import com.example.muv.ui.components.LoadingDialog
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    protected lateinit var binding: DB
    protected lateinit var viewModel: VM

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup Netflix-like UI
        setupNetflixUI()

        // Initialize DataBinding
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[viewModelClass]

        setupBaseObservers()
        setupToolbar()

        initView()
        initData()
        initListener()
    }

    /**
     * Setup Netflix-like UI - Dark theme, fullscreen
     */
    private fun setupNetflixUI() {
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set status bar color
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Setup system UI controller
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
    }

    /**
     * Show/Hide system bars for immersive experience (for video playback)
     */
    protected fun toggleSystemBars(hide: Boolean) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (hide) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun setupBaseObservers() {
        viewModel.loading.observe(this) { isLoading ->
            handleLoadingState(isLoading)
        }

        viewModel.error.observe(this) { errorMessage ->
            handleError(errorMessage)
        }

        viewModel.networkError.observe(this) { isNetworkError ->
            if (isNetworkError) {
                showNetworkError()
            }
        }

        viewModel.message.observe(this) { message ->
            showMessage(message)
        }
    }

    private fun setupToolbar() {
        // Netflix usually doesn't show back button on main screens
        if (shouldShowBackButton()) {
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
    }

    /**
     * Override this to control back button visibility
     */
    protected open fun shouldShowBackButton(): Boolean = true

    protected open fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    private fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
        }
        if (!loadingDialog!!.isAdded) {
            loadingDialog?.show(supportFragmentManager, "LoadingDialog")
        }
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
    }

    protected open fun handleError(errorMessage: String?) {
        errorMessage?.let {
            showErrorSnackbar(it)
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("retry") {
                onRetryClicked()
            }
            setBackgroundTint(getColor(R.color.netflix_red))
            setTextColor(Color.WHITE)
            setActionTextColor(Color.WHITE)
            show()
        }
    }

    private fun showNetworkError() {
        if (!isNetworkAvailable()) {
            showErrorSnackbar("error_network")
        }
    }

    protected fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    protected open fun onRetryClicked() {
        // Override in child activities if needed
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun initListener()

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.dismiss()
    }
}