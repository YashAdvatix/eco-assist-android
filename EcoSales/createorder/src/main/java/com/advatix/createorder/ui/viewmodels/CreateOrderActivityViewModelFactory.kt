package com.advatix.fep.ui.landing.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.advatix.fep.service.ApiInterface

class MainActivityViewModelFactory(private val apiInterface: ApiInterface) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(
                apiInterface = this.apiInterface
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}