package com.big9.app.utils.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class LocationUtil(private val context: Context, private val locationListener: LocationListener) {

    private val PERMISSION_ID = 123
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    interface LocationListener {
        fun onLocationReceived(latitude: Double, longitude: Double)
    }
    init {
        // Initialize mFusedLocationProviderClient in the constructor
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }
    @SuppressLint("ServiceCast")
    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (checkPermissions()) {
            mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location == null) {
                    if (isLocationEnabled()) {
                        requestNewLocationData()
                    } else {
                        // Handle when location is not enabled
                        // showSettingsAlert(...)
                    }
                } else {
                    locationListener.onLocationReceived(location.latitude, location.longitude)
                }
            }
        } else {
            requestPermissions()
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            locationListener.onLocationReceived(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }
}


