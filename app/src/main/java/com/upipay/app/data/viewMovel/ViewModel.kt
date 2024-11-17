package com.upipay.app.data.viewMovel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.big9.app.network.ResponseState
import com.upipay.app.data.model.qrModel
import com.upipay.app.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    //Qrcode Response
    val getQrCodeResponseLiveData: LiveData<ResponseState<qrModel>>
        get() = repository.getQrCodeResponseLiveData

    fun GetQrCode() {
        viewModelScope.launch {

            repository.GetQrCode()
        }
    }
}