package com.example.muv.ui.main

import com.example.muv.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {
    // MainActivity doesn't need much logic
    // Individual fragments will have their own ViewModels
}