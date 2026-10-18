package com.example.deliveryapp.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.deliveryapp.data.repository.TrackingRepository
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var repository: TrackingRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var orderId: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        orderId = intent?.getStringExtra("ORDER_ID") ?: ""
        if (orderId.isNotEmpty()) {
            startTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        serviceScope.launch {
            while (true) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            serviceScope.launch {
                                repository.updateDriverLocation(
                                    orderId,
                                    location.latitude,
                                    location.longitude
                                )
                            }
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                delay(5000)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}