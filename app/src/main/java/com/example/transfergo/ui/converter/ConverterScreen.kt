package com.example.transfergo.ui.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import org.koin.androidx.compose.koinViewModel
import com.example.transfergo.R

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsState()

    state.error?.let {
        Spacer(Modifier.height(16.dp))
        Text(it, color = MaterialTheme.colorScheme.error,style = MaterialTheme.typography.bodyLarge)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrencyRow(
                label = "Sending from",
                selectedCurrency = state.from,
                amount = state.amountSending.replace(',', '.'),
                onCurrencySelected = { newCurrency ->
                    viewModel.onFromCurrencySelected(newCurrency, state.to)
                },
                onAmountChanged = viewModel::onFromAmountChanged
            )

            Spacer(Modifier.height(8.dp))

            CurrencyRow(
                label = "Receiver gets",
                selectedCurrency = state.to,
                amount = state.amountReceiving.replace(',', '.'),
                onCurrencySelected = { newCurrency ->
                    viewModel.onToCurrencySelected(newCurrency, state.from)
                },
                onAmountChanged = viewModel::onToAmountChanged
            )
        }

        ConversionDetails(
            viewModel = viewModel,
            state = state,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ConversionDetails(
    viewModel: ConverterViewModel,
    state: ConverterUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(320.dp)
            .height(120.dp)
    ) {

        SwapButton(viewModel)

        Row(
            modifier = Modifier
                .width(200.dp)
                .height(48.dp)
                .background(
                    color = Color(0xFF000000),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ExchangeRateDisplay(state)
        }
    }
}

@Composable
private fun ExchangeRateDisplay(state: ConverterUiState) {
    if (state.rate > 0) {
        Text(
            text = "1 ${state.from} = ${"%.2f".format(state.rate).replace(',', '.')} ${state.to}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFF1F1F1)
        )
    }
}

@Composable
private fun BoxScope.SwapButton(viewModel: ConverterViewModel) {
    Image(
        painter = painterResource(id = R.drawable.reverse_button_foreground),
        contentDescription = "Swap currencies",
        modifier = Modifier
            .offset(x = 16.dp)
            .size(64.dp)
            .clickable { viewModel.onSwap() }
            .padding(8.dp)
            .align(Alignment.CenterStart)
    )
}