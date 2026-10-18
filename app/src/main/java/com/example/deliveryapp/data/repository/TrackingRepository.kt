package com.example.deliveryapp.data.repository

import com.example.deliveryapp.domain.model.DeliveryOrder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getLiveLocation(orderId: String): Flow<DeliveryOrder> = callbackFlow {
        val listener = firestore.collection("deliveries").document(orderId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val order = snapshot.toObject(DeliveryOrder::class.java)
                    if (order != null) trySend(order)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateDriverLocation(orderId: String, lat: Double, lng: Double) {
        firestore.collection("deliveries").document(orderId)
            .update(mapOf("lat" to lat, "lng" to lng)).await()
    }
}