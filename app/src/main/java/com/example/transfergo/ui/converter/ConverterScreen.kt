package com.example.transfergo.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrencyRow(
            label = "Sending from",
            selectedCurrency = state.from,
            amount = state.amountSending,
            onCurrencySelected = viewModel::onFromCurrencySelected,
            onAmountChanged = viewModel::onAmountChanged
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = { viewModel.onSwap() }) {
            Text("🔄 Swap")
        }

        Spacer(Modifier.height(12.dp))


        CurrencyRow(
            label = "Receiver gets",
            selectedCurrency = state.to,
            amount = state.amountReceiving,
            onCurrencySelected = viewModel::onToCurrencySelected,
            onAmountChanged = {}
        )

        Spacer(Modifier.height(16.dp))

        if (state.rate > 0) {
            Text("1 ${state.from} = ${"%.3f".format(state.rate)} ${state.to}")
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}