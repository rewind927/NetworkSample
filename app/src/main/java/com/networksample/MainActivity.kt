package com.networksample

import android.content.Context
import android.database.ContentObserver
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.networksample.APIUtils.Companion.PRIVATE_API_URL
import com.networksample.APIUtils.Companion.PUBLIC_API_URL
import com.networksample.repository.StatusRepository
import com.networksample.viewmodel.MainViewModel
import com.networksample.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var viewModel: MainViewModel

    private var currentAPI = PRIVATE_API_URL

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.e("123", "onAvailable!!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                refreshCurrentNetworkRequestUrl()
            }
        }
    }

    private val observer: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.w("123", "onChange : ${checkMobileNetworkIsON()}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                refreshCurrentNetworkRequestUrl()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        registerNetworkStatus()
        initUI()
        observeViewModelData()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(callback)
        contentResolver.unregisterContentObserver(observer)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(StatusRepository())
        ).get(MainViewModel::class.java)
    }

    private fun registerNetworkStatus() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        }

        val mobileDataSettingUri = Settings.Secure.getUriFor("mobile_data")
        applicationContext
            .contentResolver
            .registerContentObserver(
                mobileDataSettingUri, true,
                observer
            )
    }

    private fun initUI() {
        button_request.setOnClickListener {
            viewModel.requestData(currentAPI)
        }
    }

    private fun observeViewModelData() {
        viewModel.showToast.observe(this,
            Observer { string ->
                Toast.makeText(this@MainActivity, string, Toast.LENGTH_SHORT).show()
            })
    }

    private fun checkMobileNetworkIsON() =
        Settings.Secure.getInt(contentResolver, "mobile_data", 1) == 1


    @RequiresApi(Build.VERSION_CODES.M)
    private fun refreshCurrentNetworkRequestUrl() {
        val activeNetwork = connectivityManager.activeNetwork
        val caps =
            connectivityManager.getNetworkCapabilities(activeNetwork)
        val cellular = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val wifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

        currentAPI = if (cellular == true || (wifi == true && checkMobileNetworkIsON())) {
            PRIVATE_API_URL
        } else {
            PUBLIC_API_URL
        }
    }
}
