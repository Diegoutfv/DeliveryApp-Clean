package com.example.deliveryapp.data.repository

import android.content.Context
import com.example.deliveryapp.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Guarda el perfil de entrega en el teléfono, separado por usuario de Firebase. */
@Singleton
class ProfileRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val auth: FirebaseAuth
) {
    private val prefs = context.getSharedPreferences("rosso_profile", Context.MODE_PRIVATE)

    private fun key(field: String) = "${auth.currentUser?.uid ?: "guest"}_$field"

    val email: String
        get() = auth.currentUser?.email.orEmpty()

    fun load(): UserProfile = UserProfile(
        name = prefs.getString(key("name"), "").orEmpty(),
        phone = prefs.getString(key("phone"), "").orEmpty(),
        address = prefs.getString(key("address"), "").orEmpty()
    )

    fun save(profile: UserProfile) {
        prefs.edit()
            .putString(key("name"), profile.name.trim())
            .putString(key("phone"), profile.phone.trim())
            .putString(key("address"), profile.address.trim())
            .apply()
    }
}
