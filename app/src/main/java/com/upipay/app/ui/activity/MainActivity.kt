package com.upipay.app.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.upipay.app.R
import com.upipay.app.data.viewMovel.ViewModel
import com.upipay.app.databinding.ActivityMainBinding
import com.upipay.app.databinding.ActivityNetworkBinding
import com.upipay.app.utils.helper.ScreenshotUtils.Companion.takeScreenshot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]


        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    fun shareImage(screenshotBitmap: Bitmap) {
        takeScreenshot(this, screenshotBitmap)
        /* val mediaProjectionManager =
             getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
         startActivityForResult(
             mediaProjectionManager.createScreenCaptureIntent(),
             REQUEST_MEDIA_PROJECTION
         )*/
    }

}