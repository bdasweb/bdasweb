package com.upipay.app.ui.fragment.upi_qr

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.big9.app.network.ResponseState

import com.big9.app.utils.common.LocationUtil

import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.upipay.app.R

import com.upipay.app.data.viewMovel.ViewModel
import com.upipay.app.databinding.FragmentQrBinding
import com.upipay.app.ui.activity.MainActivity
import com.upipay.app.ui.base.BaseFragment
import com.upipay.app.utils.common.MethodClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
@AndroidEntryPoint
class UpiQrFragment : BaseFragment(), LocationUtil.LocationListener {
    private var loader: Dialog? = null
    lateinit var binding: FragmentQrBinding
    private val viewModel: ViewModel by activityViewModels()
    private var isFabOpen = false
    var UPI_PAYMENT=12
    //var upiLink="upi://pay?pa=yespay.gllsiop0000039@yesbankltd&pn=FAST%20SOLUTIONS%20FINTECH%20PVTLTD&mc=4214&tr=TRN2411151739575599&tn=FFFFLPISLVUQPRX9&am=20&cu=INR&url=https://indiaonlinepay.com"
    var upiLink=""
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(inflater, container, false)
       // binding.viewModel = viewModel
        binding.lifecycleOwner = this

        init()
        viewOnClick()
        observer()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun observer() {
        viewModel?.getQrCodeResponseLiveData?.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseState.Loading -> {
                    binding.qrCodeImageView.visibility=View.INVISIBLE
                    loader?.show()
                }

                is ResponseState.Success -> {
                    loader?.dismiss()
                        lifecycleScope.launch {
                            response?.data?.UpiLink?.let {
                                binding.qrCodeImageView.visibility=View.VISIBLE
                                upiLink=it
                                generateQRCode(upiLink)
                            }?:{
                                binding.qrCodeImageView.visibility=View.GONE
                                Toast.makeText(binding.root.context, "No UPI", Toast.LENGTH_SHORT).show()
                            }


                                binding.fabMenu.setImageResource(R.drawable.plus)

                        }

                }

                is ResponseState.Error -> {
                    loader?.dismiss()
                    response?.apply {
                        handleApiError(isNetworkError, errorCode, errorMessage)
                    }

                    handleApiError(
                        response.isNetworkError,
                        response.errorCode,
                        response.errorMessage
                    )

                }


            }
        }
    }

    private fun viewOnClick() {
        binding?.apply {
            fabMenu.setOnClickListener {
                toggleFabMenu()
            }

            tvRefresh.setOnClickListener{
                viewModel?.GetQrCode()
            }

            fabShare.setOnClickListener {
                shareQRCode()
            }

            fabDownload.setOnClickListener {
                downloadQRCode()
            }
            imgBack.setOnClickListener{
                findNavController().popBackStack()
            }
            tvUpiIdCopy.setOnClickListener {
                val textToCopy = tvUpiIdCopy.text.toString()
                copyToClipboard(textToCopy)
            }

            qrCodeImageView.setOnClickListener {
                payUsingUpi()
            }
            tvUpilink.setOnClickListener {
                payUsingUpi()
            }




        }



    }

    private fun copyToClipboard(text: String) {
        val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)

        // Show a toast message to indicate the text is copied
        Toast.makeText(binding.root.context, "Copied! You can now paste the UPI ID wherever needed.", Toast.LENGTH_SHORT).show()
    }

    private fun toggleFabMenu() {
        if (isFabOpen) {
            binding.fabShare.visibility = View.GONE
            binding.fabDownload.visibility = View.GONE
            binding.fabMenu.setImageResource(R.drawable.plus) // Replace with your icon
        } else {
            binding.fabShare.visibility = View.VISIBLE
            binding.fabDownload.visibility = View.VISIBLE
            binding.fabMenu.setImageResource(R.drawable.close_menu) // Replace with your icon
        }
        isFabOpen = !isFabOpen
    }

    private fun shareQRCode() {
        shareImage()
        /*val bitmap = getQRCodeBitmap()
        if (bitmap != null) {
            val file = File(requireContext().cacheDir, "qr_code.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
            }
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
        }*/
    }

    private fun downloadQRCode() {
        //val bitmap = getQRCodeBitmap()
        var screenshotBitmap =binding.cardView2.takeScreenshot()
        screenshotBitmap?.let {
            saveBitmapToDownloads(binding.root.context, it)
        }
        /*if (bitmap != null) {
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                "qr_code",
                "UPI QR Code"
            )
            Toast.makeText(requireContext(), "QR Code downloaded!", Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun getQRCodeBitmap(): Bitmap? {
        return (binding.qrCodeImageView.drawable as? BitmapDrawable)?.bitmap
    }

    private fun init() {
        activity?.let {
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
        }

        viewModel?.GetQrCode()
        //generateQRCode(upiLink)
        binding.tvUpiIdCopy.visibility=View.GONE
        setUpiTextVisibleOrNot()


    }

    private fun setUpiTextVisibleOrNot() {
        if(binding.tvUpiIdCopy.text.toString().isNotEmpty())
        {
            binding.tvUpiIdCopy.visibility=View.VISIBLE
        }

    }

    override fun onLocationReceived(latitude: Double, longitude: Double) {
        // Handle location updates here
    }

    private fun generateQRCode(upiLink: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(upiLink, BarcodeFormat.QR_CODE, 400, 400)
            binding.qrCodeImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveBitmapToDownloads(context: Context, bitmap: Bitmap) {
        val appName = getString(R.string.app_name)
        val fileName = "${appName}_qr_${System.currentTimeMillis()}.jpg"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Notify the system that a new file has been created
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
            loader?.dismiss()
            Toast.makeText(context, "Saved to Downloads folder", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            loader?.dismiss()
            Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }


    private fun shareImage() {
        activity?.let {
            binding.apply {
                var screenshotBitmap =cardView2.takeScreenshot()
                (activity as? MainActivity)?.shareImage(screenshotBitmap)
            }
        }
    }

    fun View.takeScreenshot(): Bitmap {
        // Create a Bitmap with the same dimensions as the View
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw the View onto the Bitmap
        val canvas = Canvas(bitmap)

        // Draw the View onto the Canvas
        this.draw(canvas)

        return bitmap
    }

    //private fun payUsingUpi(name: String, upiId: String, note: String, amount: String) {
    private fun payUsingUpi() {
       /* val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR") // Currency
            .build()*/

            //val uri = Uri.parse("upi://pay?pa=MSBPROSOFTECHPRIVATELIMITEDFRMYKNASBDASFINTECHPRIVATELIMITED.eazypay@icici&pn=M/S.BPRO%20SOFTECH%20PRIVATE%20LIMITED(FRMY%20KN%20AS%20BDAS%20FINTECH%20PRIVATE%20LIMITED)%20&tr=EZYS9051333222&cu=INR&mc=7299")
            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            //upiPayIntent.data = uri
            upiPayIntent.data =  Uri.parse(upiLink)

            // Always check if there are any UPI apps installed
            val chooser = Intent.createChooser(upiPayIntent, "Pay with")
            if (chooser.resolveActivity(binding.root.context.packageManager) != null) {
                if(binding.qrCodeImageView.isVisible==true) {
                    startActivityForResult(chooser, UPI_PAYMENT)
                }
            } else {
                Toast.makeText(binding.root.context, "No UPI app found", Toast.LENGTH_SHORT).show()
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("UPI_Response", "onActivityResult: resultCode $resultCode")
        // Print all data from the Intent
        val bundle = data?.extras
        if (bundle != null) {
            for (key in bundle.keySet()) {
                Log.d("UPI_Response", "@$key: ${bundle.get(key)}")
            }
        } else {
            Log.d("UPI_Response", "No extras found in the Intent.")
        }
        when (requestCode) {
            UPI_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK || resultCode == 11) {
                    if (data != null) {
                        val responseText = data.getStringExtra("response")
                        if (responseText.isNullOrEmpty()) {
                            Toast.makeText(binding.root.context, "No response from UPI app. Please check your transaction history.", Toast.LENGTH_SHORT).show()
                            Log.e("UPI", "Empty or null response from UPI app.")
                        } else {
                            processUpiPaymentResponse(responseText)
                        }
                    } else {
                        Toast.makeText(binding.root.context, "No response from UPI app. Please check manually.", Toast.LENGTH_SHORT).show()
                        Log.e("UPI", "Intent data is null, no response from UPI app.")
                    }
                } else {
                    Toast.makeText(binding.root.context, "UPI Payment Failed or Canceled", Toast.LENGTH_SHORT).show()
                    Log.e("UPI", "Transaction was not successful, resultCode: $resultCode")
                }
            }
        }
    }

    private fun processUpiPaymentResponse(responseText: String?) {
        val responseArray = responseText?.split("&") ?: listOf()
        var status = ""
        var approvalRefNo = ""
        for (response in responseArray) {
            val keyValue = response.split("=")
            if (keyValue.size >= 2) {
                when (keyValue[0].lowercase()) {
                    "status" -> status = keyValue[1].lowercase()
                    "approvalrefno", "txnref" -> approvalRefNo = keyValue[1]
                }
            }
        }

        when (status) {
            "success" -> Toast.makeText(binding.root.context, "Payment Successful", Toast.LENGTH_SHORT).show()
            "failure" -> Toast.makeText(binding.root.context, "Payment Failed", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(binding.root.context, "Payment Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


}



