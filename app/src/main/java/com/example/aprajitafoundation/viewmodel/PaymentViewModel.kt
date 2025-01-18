package com.example.aprajitafoundation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PaymentViewModel(): ViewModel() {

    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled: StateFlow<Boolean> get() = _isButtonEnabled

    // Method to validate the form and update button state
    fun validateForm(name: String, contact: String, amount: String?) {
        val isNameValid = name.isNotBlank()
        val isContactValid = contact.matches(Regex("^[0-9]{10}$"))
        val isAmountValid = !amount.isNullOrBlank() && (amount.toFloatOrNull() ?: 0f) > 0

        // Update button state based on form validation
        _isButtonEnabled.value = isNameValid && isContactValid && isAmountValid
    }
}