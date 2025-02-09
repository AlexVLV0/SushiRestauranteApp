package com.example.sushirestauranteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RestauranteViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestauranteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestauranteViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
