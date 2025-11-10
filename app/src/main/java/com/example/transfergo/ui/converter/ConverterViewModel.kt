package com.example.transfergo.ui.converter

import androidx.lifecycle.ViewModel
import com.example.transfergo.data.repository.FxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConverterViewModel(
    private val repository: FxRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(())
    val uiState = _uiState.asStateFlow()
}
