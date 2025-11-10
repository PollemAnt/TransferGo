package com.example.transfergo.ui.converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsState()

}