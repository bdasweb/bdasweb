package com.upipay.app.ui.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.upipay.app.R
import com.upipay.app.databinding.ActivityNetworkBinding
import com.upipay.app.utils.helper.NoNetworkReceiver

import com.upipay.app.utils.interfaces.NetworkCallBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetworkActivity : AppCompatActivity() {

    lateinit var nonetwork : NoNetworkReceiver
    private lateinit var binding: ActivityNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityNetworkBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_network)
        /*nonetwork = NoNetworkReceiver(object : NetworkConnectionListener {
            override fun onNetworkConnection(connectionStatus: Boolean) {
                if (connectionStatus) {
                    finish()
                }
            }

        })*/
        nonetwork=NoNetworkReceiver(object : NetworkCallBack {
            override fun hasNetwork(hasnetwork: Boolean) {
                if(hasnetwork){
                   finish()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(nonetwork, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(nonetwork)
    }
}