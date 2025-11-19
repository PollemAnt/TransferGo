package com.example.transfergo.ui.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.transfergo.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
        ErrorMessage(state.error)

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ErrorMessage(errorMessage: String?) {
    errorMessage?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = errorMessage,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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