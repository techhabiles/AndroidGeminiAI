package com.example.geminiintegration


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * BaseViewModel to handle loading state , all the ViewModels show inherit this class to provide consistant loading indicator experience
 * @author TechHabiles
 */

open class BaseViewModel: ViewModel() {
    private val _isLoading: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}