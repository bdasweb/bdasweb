package com.upipay.app.ui.base

import android.app.Application
import android.content.Context


import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class PayConApplication: Application() {
    init {
        instance = this
    }
    companion object {
        lateinit var instance: PayConApplication
        lateinit var globalContext: Context
            private set
    }
    override fun onCreate() {
        super.onCreate()
        globalContext = applicationContext

    }

    fun contextReturn():Context{
        return globalContext
    }
}