package com.example.transfergo.ui.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.transfergo.R

@Composable
fun CurrencyRow(
    label: String,
    selectedCurrency: String,
    amount: String,
    onCurrencySelected: (String) -> Unit,
    onAmountChanged: (String) -> Unit
) {
    val currencies = listOf("PLN", "GER", "GBP", "UAH")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val flagRes = when (selectedCurrency) {
                    "PLN" -> R.drawable.flag_pl
                    "GER" -> R.drawable.flag_de
                    "GBP" -> R.drawable.flag_gb
                    "UAH" -> R.drawable.flag_ua
                    else -> R.drawable.flag_placeholder
                }

                Image(
                    painter = painterResource(flagRes),
                    contentDescription = selectedCurrency,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 6.dp)
                )

                Text(selectedCurrency, style = MaterialTheme.typography.titleMedium)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.filter { it != selectedCurrency }.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChanged,
            label = { Text(label) },
            modifier = Modifier.weight(1f)
        )
    }
}

