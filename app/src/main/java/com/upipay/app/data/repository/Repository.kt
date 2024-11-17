package com.upipay.app.data.repository


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.big9.app.network.ResponseState
import com.upipay.app.data.model.qrModel
import com.upipay.app.data.network.RetroApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Repository @Inject constructor(private val api: RetroApi,
                                     @ApplicationContext private val context: Context
) {

    //Qrcode Response
    private val _GetQrCodeResponseLiveData =
        MutableLiveData<ResponseState<qrModel>>()
    val getQrCodeResponseLiveData: LiveData<ResponseState<qrModel>>
        get() = _GetQrCodeResponseLiveData


    suspend fun GetQrCode() {
        _GetQrCodeResponseLiveData.postValue(ResponseState.Loading())
        try {

            val response =
                api.GetQrCode()
            _GetQrCodeResponseLiveData.postValue(ResponseState.create(response))
            Log.d("TAG_abc", "GetQrCode: code "+response.code())
        } catch (throwable: Throwable) {
            Log.d("TAG_abc", "GetQrCode: def "+throwable.message)
            _GetQrCodeResponseLiveData.postValue(ResponseState.create(throwable))
        }
    }
 }






