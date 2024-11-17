package com.upipay.app.data.network

import com.upipay.app.data.model.qrModel
import com.upipay.app.data.network.urls.getQRCode
import retrofit2.Response
import retrofit2.http.GET

interface RetroApi {

   @GET(getQRCode)
    suspend fun GetQrCode(
    ): Response<qrModel>

}