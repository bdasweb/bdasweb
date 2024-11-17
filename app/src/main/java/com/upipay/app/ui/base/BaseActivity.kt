package com.upipay.app.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.upipay.app.BuildConfig
import com.upipay.app.ui.activity.NetworkActivity
import com.upipay.app.utils.helper.NoNetworkReceiver
import com.upipay.app.utils.interfaces.NetworkCallBack


import dagger.hilt.android.AndroidEntryPoint
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@AndroidEntryPoint
open class BaseActivity: AppCompatActivity()/*, NetworkConnectionListener */{
   lateinit var nonetwork : NoNetworkReceiver
    /*@Inject
    lateinit var analytics: FirebaseAnalytics*/

   // lateinit var  database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseAnalytics.getInstance(EpayApplication.globalContext).setAnalyticsCollectionEnabled(true)
        /*nonetwork = NoNetworkReceiver(object :NetworkConnectionListener{
            override fun onNetworkConnection(connectionStatus: Boolean) {
                *//*if (!MethodClass.check_networkconnection(this@BaseActivity)){
                    startActivity(Intent(this@BaseActivity, NetworkActivity::class.java))
                }*//*
                if(connectionStatus){
                    startActivity(Intent(this@BaseActivity, NetworkActivity::class.java))
                }

            }

        })*/
       // database= Firebase.database
        nonetwork = NoNetworkReceiver(object : NetworkCallBack {
            override fun hasNetwork(hasnetwork: Boolean) {
                if(!hasnetwork){
                   startActivity(Intent(this@BaseActivity, NetworkActivity::class.java))

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


    /* override fun onNetworkConnection(connectionStatus: Boolean) {
         if (!MethodClass.check_networkconnection(this))
             startActivity(Intent(this, NetworkActivity::class.java))
     }*/


    fun Activity.showLog(tag:String="TAG_",value:String="property_value"){
        var tagData=if (tag=="TAG_"){
            "TAG_${this.javaClass.simpleName}"
        }
        else{
            tag
        }
        if (BuildConfig.DEBUG) {
            Log.d(tagData, value)
        }

    }




    /*fun Activity.storeErrorData(){

        MethodClass.callFirebaseDatabase(this as Context,database)
    }*/
}