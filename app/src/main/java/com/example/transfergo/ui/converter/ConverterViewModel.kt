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
import kotlinx.coroutines.flow.update
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
    private var isFromAmountChanging = true

    init {
        triggerConversion()
    }

    fun onFromAmountChanged(newAmount: String) {
        val normalizedAmount = newAmount.replace(',', '.')
        isFromAmountChanging = true
        _uiState.value = _uiState.value.copy(amountSending = normalizedAmount)
        triggerConversion()
    }

    fun onToAmountChanged(newAmount: String) {
        val normalizedAmount = newAmount.replace(',', '.')
        isFromAmountChanging = false
        _uiState.value = _uiState.value.copy(amountReceiving = normalizedAmount)
        triggerConversion()
    }

    fun onSwap() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                from = _uiState.value.to,
                to = _uiState.value.from,
                amountSending = _uiState.value.amountReceiving,
                amountReceiving = _uiState.value.amountSending
            )

            triggerConversionAfterSwap()
        }
    }

    private fun triggerConversionAfterSwap() {
        job?.cancel()
        job = viewModelScope.launch {
            val amount =
                _uiState.value.amountSending.replace(',', '.').toDoubleOrNull() ?: return@launch
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
                    _uiState.value = _uiState.value.copy(error = result.error)
                }
            }
        }
    }

    private fun triggerConversion() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(400) // debounce

            if (isFromAmountChanging) {
                val amount = _uiState.value.amountSending.toDoubleOrNull() ?: return@launch
                val limit = SEND_LIMITS[_uiState.value.from] ?: Double.MAX_VALUE
                if (amount > limit) {
                    _uiState.value =
                        _uiState.value.copy(error = "Limit exceeded for ${_uiState.value.from}")
                    return@launch
                }
                when (val result =
                    repository.convert(_uiState.value.from, _uiState.value.to, amount)) {
                    is FxResult.Success -> {

                        _uiState.value = _uiState.value.copy(
                            rate = result.data.first.toString().replace(',', '.').toDouble(),
                            amountReceiving = "%.2f".format(result.data.second).replace(',', '.'),
                            error = null,
                        )
                    }

                    is FxResult.Error -> {

                        _uiState.value = _uiState.value.copy(error = result.error)
                    }
                }
            } else {
                val amount = _uiState.value.amountReceiving.toDoubleOrNull() ?: return@launch
                when (val result =
                    repository.convert(_uiState.value.to, _uiState.value.from, amount)) {
                    is FxResult.Success -> {

                        val convertedAmount = result.data.second
                        val limit = SEND_LIMITS[_uiState.value.from] ?: Double.MAX_VALUE
                        if (convertedAmount > limit) {
                            _uiState.value =
                                _uiState.value.copy(error = "Limit exceeded for ${_uiState.value.from}")
                            return@launch
                        }
                        _uiState.value = _uiState.value.copy(
                            amountSending = "%.2f".format(convertedAmount),
                            error = null,
                        )
                    }

                    is FxResult.Error -> {

                        _uiState.value = _uiState.value.copy(error = result.error)
                    }
                }
            }
        }
    }

    fun onFromCurrencySelected(newCurrency: String, currentToCurrency: String) {
        viewModelScope.launch {
            if (newCurrency == currentToCurrency) {
                _uiState.update { it.copy(from = newCurrency, to = it.from) }
            } else {
                _uiState.update { it.copy(from = newCurrency) }
            }
            triggerConversion()
        }
    }

    fun onToCurrencySelected(newCurrency: String, currentFromCurrency: String) {
        viewModelScope.launch {
            if (newCurrency == currentFromCurrency) {
                _uiState.update { it.copy(to = newCurrency, from = it.to) }
            } else {
                _uiState.update { it.copy(to = newCurrency) }
            }
            triggerConversion()
        }
    }
}