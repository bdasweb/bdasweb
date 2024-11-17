package com.upipay.app.data.model

import com.google.gson.annotations.SerializedName

data class qrModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("response_code") var responseCode: String? = null,
    @SerializedName("orderid") var orderid: String? = null,
    @SerializedName("UpiLink") var UpiLink: String? = null

)
