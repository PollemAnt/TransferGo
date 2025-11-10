package com.example.transfergo.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transfergo.data.repository.FxRepository
import com.example.transfergo.util.FxResult
import com.example.transfergo.util.SEND_LIMITS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConverterUiState(
    val from: String = "PLN",
    val to: String = "UAH",
    val amountSending: String = "300.00",
    val amountReceiving: String = "",
    val rate: Double = 0.0,
    val error: String? = null
)



class ConverterViewModel(
    private val repository: FxRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    fun onAmountChanged(newAmount: String) {
        _uiState.value = _uiState.value.copy(amountSending = newAmount)
        triggerConversion()
    }

    fun onSwap() {
        _uiState.value = _uiState.value.copy(
            from = _uiState.value.to,
            to = _uiState.value.from
        )
        triggerConversion()
    }

    private fun triggerConversion() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(400) // debounce
            val amount = _uiState.value.amountSending.toDoubleOrNull() ?: return@launch
            val limit = SEND_LIMITS[_uiState.value.from] ?: Double.MAX_VALUE
            if (amount > limit) {
                _uiState.value =
                    _uiState.value.copy(error = "Limit exceeded for ${_uiState.value.from}")
                return@launch
            }
            when (val result = repository.convert(_uiState.value.from, _uiState.value.to, amount)) {
                is FxResult.Success -> {

                    _uiState.value = _uiState.value.copy(
                        rate = result.data.first,
                        amountReceiving = "%.2f".format(result.data.second),
                        error = null,
                    )
                }

                is FxResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = "Conversion failed")
                }
            }
        }
    }

    fun onFromCurrencySelected(code: String) {
        _uiState.value = _uiState.value.copy(from = code)
        triggerConversion()
    }

    fun onToCurrencySelected(code: String) {
        _uiState.value = _uiState.value.copy(to = code)
        triggerConversion()
    }
}