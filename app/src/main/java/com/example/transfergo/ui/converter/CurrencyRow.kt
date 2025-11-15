package com.example.transfergo.ui.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.transfergo.R
import com.example.transfergo.util.SUPPORTED_CURRENCIES

@Composable
fun CurrencyRow(
    label: String,
    selectedCurrency: String,
    amount: String,
    onCurrencySelected: (String) -> Unit,
    onAmountChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = Color(0xF5B7B7B7),
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val flagRes = when (selectedCurrency) {
                    "PLN" -> R.drawable.flag_pl
                    "EUR" -> R.drawable.flag_de
                    "GBP" -> R.drawable.flag_gb
                    "UAH" -> R.drawable.flag_ua
                    else -> R.drawable.flag_placeholder
                }

                Image(
                    painter = painterResource(flagRes),
                    contentDescription = selectedCurrency,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 8.dp)
                )

                Text(
                    text = selectedCurrency,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                SUPPORTED_CURRENCIES.forEach { currency ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = currency,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
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
            onValueChange = { newValue ->
                if (isValidDecimalInput(newValue)) {
            onAmountChanged(newValue)
        }
            },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )
    }
}

private fun isValidDecimalInput(input: String): Boolean {
    if (input.isEmpty()) return true
    val dotCount = input.count { it == '.' }
    val containsOnlyDigitsAndDot = input.all { it.isDigit() || it == '.' }
    if (dotCount > 1) return false
    if (!containsOnlyDigitsAndDot) return false
    if (input.startsWith('.')) return false

    return true

}

