package com.big9.app.network

import android.util.Log
import com.google.gson.stream.MalformedJsonException

import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class ResponseState<T>(
    val data: T? = null,
    val isNetworkError: Boolean = false,
    val errorMessage: String? = null,
    val errorCode: Int? = null
) {

    class Loading<T> : ResponseState<T>()
    class Success<T>(data: T? = null) : ResponseState<T>(data = data)
    class Error<T>(isNetworkError: Boolean, errorMessage: String?, errorCode: Int?) :
        ResponseState<T>(isNetworkError = isNetworkError, errorMessage = errorMessage, errorCode = errorCode)

    companion object {
        private const val TAG: String = "AppDebug"

        fun <T> create(throwable: Throwable): Error<T> {
            return when(throwable){
                is HttpException -> {
                    //////Log.d(TAG, "GenericApiResponse: Error: ${throwable.code()}: ${throwable.message()}")
                    Error(false, "Response Time out . Contact CC immediatly to get this issue resolve"  , throwable.code())
                    //Error(false, throwable.message(), throwable.code())
                }
                is MalformedJsonException->{
                    Error(false, "Response service error. Contact CC immediatly to get this issue resolve", 500)
                }
                is IOException -> {
                    //network error
                    //////Log.d(TAG, "GenericApiResponse: Network ")
                    if(throwable.message.toString().trim().lowercase()=="timeout"){
                        Error(false, "Response Time out . Contact CC immediatly to get this issue resolve", 500)
                    }
                    else{
                        Error(true, null, null)
                    }
                    //Error(true, null, null)
                    //////_errorcode", "GenericApiResponse: Network "+throwable.message)

                }



                else -> {
                    //other exceptions
                    //////Log.d(TAG, "GenericApiResponse: other Error")
                    //Error(false, throwable.message, null)
                    Error(false, "Response service error . Contact CC immediatly to get this issue resolve", 500)
                }
            }
        }

        fun <T> create(response: Response<T>): ResponseState<T> {

            /*////Log.d(TAG, "GenericApiResponse: response: ${response.body().toString()}")
            ////Log.d(TAG, "GenericApiResponse: response: ${response.body().toString()}")
            ////Log.d(TAG, "GenericApiResponse: raw: ${response.raw()}")
            ////Log.d(TAG, "GenericApiResponse: headers: ${response.headers()}")
            ////Log.d(TAG, "GenericApiResponse: message: ${response.message()}")*/
            var responseCode=response.code()
            Log.d("TAG_checkdata", "create: data "+response.body())
            return if((response.isSuccessful && response.body()!=null)/* ||(responseCode==205 && response.errorBody()!=null) */){
                // success
//                val successResponse = Gson().toJson(response.body())
//                val jObj = JSONObject(successResponse)
//                val mResponse = jObj.optJSONObject("response")
//                val raws = mResponse?.optJSONObject("raws")
//                val data = raws?.optJSONObject("data")
//                val token = data?.optString("token")
//                if (!token.isNullOrEmpty()) {
//                    userSharedPref.setUserToken(token)
//                }
                Success(response.body())
            }

            else {
                //Error
                val errorObj =
                    JSONObject(response.errorBody()?.charStream()?.readText()?:"{}")
                /*val description = errorObj.optJSONObject("Description")*/
                //val responseStatus = responseObj?.optJSONObject("status")
                //val errorMessage = responseStatus?.optString("msg")

                //val errorObj = JSONObject(response.errorBody()?.charStream()?.readText() ?: "{}")
                val description = errorObj.optString("Description", "")

                val errorCode = response.code()
                Error(false,description,errorCode)
            }
        }
    }
}

