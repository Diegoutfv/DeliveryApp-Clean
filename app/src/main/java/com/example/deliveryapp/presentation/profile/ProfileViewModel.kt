package com.example.deliveryapp.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.deliveryapp.data.repository.ProfileRepository
import com.example.deliveryapp.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileUiState(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val justSaved: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun load() {
        val profile = repository.load()
        _state.value = ProfileUiState(
            email = repository.email,
            name = profile.name,
            phone = profile.phone,
            address = profile.address
        )
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, justSaved = false) }

    fun onPhoneChange(value: String) = _state.update {
        it.copy(phone = value.filter { c -> c.isDigit() || c == '+' || c == ' ' }, justSaved = false)
    }

    fun onAddressChange(value: String) = _state.update { it.copy(address = value, justSaved = false) }

    fun save() {
        val s = _state.value
        repository.save(UserProfile(s.name, s.phone, s.address))
        _state.update { it.copy(justSaved = true) }
    }

    fun messageShown() = _state.update { it.copy(justSaved = false) }
}
