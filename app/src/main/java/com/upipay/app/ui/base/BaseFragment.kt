package com.upipay.app.ui.base


import android.content.Context
import androidx.fragment.app.Fragment
import com.upipay.app.ui.popup.ErrorPopUp
import com.upipay.app.utils.common.MethodClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {
    override fun onStart() {
        super.onStart()

    }


    fun Fragment.handleApiError(
        isNetworkError: Boolean,
        errorCode: Int?,
        errorMessage: String?,
        isShowPopup: Boolean = true
    ) {
        context?.let {
            /*var errorMessageData = errorMessage
            if (errorMessage?.lowercase()?.contains("authentication") == true  && errorCode!=408) {
                errorMessageData = getString(R.string.something_went_wrong_please_try_again_later)
            }*/

            // if (it is Activity && !it.isFinishing) {
            try {
                handleError(it, isNetworkError, errorCode, errorMessage, isShowPopup)
            }catch (e:Exception){
                var errorData="{\n" +
                        "  \"isNetworkError\": $isNetworkError,\n" +
                        "  \"errorCode\": $errorCode,\n" +
                        "  \"errorMessage\": $errorMessage,\n" +
                        "  \"isShowPopup\": $isShowPopup,\n" +
                        "  \"exception\": ${e.message},\n" +
                        "  \"exception_where\": fragment retrofit helper,\n" +
                        "  \n" +
                        "}"




            }
            //}
        }
    }

    private fun handleError(
        context: Context,
        isNetworkError: Boolean,
        errorCode: Int?,
        errorMessage: String?,
        isShowPopup: Boolean,
    ) {

        if (isNetworkError) {
            // network error
            // ErrorPopUp(context).showMessageDialog(context.getString(R.string.network_error))
        } else {
            ErrorPopUp(context).showMessageDialog(errorMessage)
            /*////_code", "handleError: "+errorCode)
            when (errorCode) {

                400 -> {
                    if (isShowPopup)
                        ErrorPopUp(context).showMessageDialog(errorMessage)
                }

                401 -> {
                    context.userLogout()
                    //Required Field Missing
                    ErrorPopUp(context).showMessageDialog("You are already logged in on another device.")
                    //session logout
                    //    context.userLogout()
                }

                402 -> {
                    //Required Field Missing
                    ErrorPopUp(context).showMessageDialog(errorMessage)
                    //session logout
                    //    context.userLogout()
                }
                403 -> {

                    //App Update Required
                    //  context.appUpdateRequired()
                }
                407 -> {
                    //session logout
                    context.userLogout()
                }
                408 -> {
                    ErrorPopUp(context).showMessageDialog(errorMessage)
                }
                500 -> {

                    //ErrorPopUp(context).showMessageDialog("Response service error 500.")
                    ErrorPopUp(context).showMessageDialog(errorMessage)
                }*/


            }
        }
    }














