package com.example.transfergo.ui

import com.example.transfergo.ui.converter.ConverterScreen
import com.example.transfergo.ui.converter.ConverterUiState
import com.example.transfergo.ui.converter.ConverterViewModel
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import io.mockk.verify
import io.mockk.every
import androidx.compose.ui.test.assertIsDisplayed

class ConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: ConverterViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ConverterUiState>

    @Before
    fun setUp() {
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ConverterUiState())

        every { mockViewModel.uiState } returns uiStateFlow
    }

    @Test
    fun should_display_initial_ui_elements() {
        // Given
        val initialState = ConverterUiState(
            from = "EUR",
            to = "PLN",
            amountSending = "100.0",
            amountReceiving = "450.0",
            rate = 4.5
        )
        uiStateFlow.value = initialState

        // When
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Sending from").assertIsDisplayed()
        composeTestRule.onNodeWithText("Receiver gets").assertIsDisplayed()
        composeTestRule.onNodeWithText("GER").assertIsDisplayed()
        composeTestRule.onNodeWithText("PLN").assertIsDisplayed()
        composeTestRule.onNodeWithText("100.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("450.0").assertIsDisplayed()
    }

    @Test
    fun should_display_exchange_rate_when_rate_is_positive() {
        // Given
        val stateWithRate = ConverterUiState(
            from = "GER",
            to = "PLN",
            rate = 4.5
        )
        uiStateFlow.value = stateWithRate

        // When
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("1 EUR = 4.50 PLN").assertIsDisplayed()
    }

    @Test
    fun should_not_display_exchange_rate_when_rate_is_zero() {
        // Given
        val stateWithoutRate = ConverterUiState(
            from = "GER",
            to = "PLN",
            rate = 0.0
        )
        uiStateFlow.value = stateWithoutRate

        // When
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("1 EUR =").assertDoesNotExist()
    }

    @Test
    fun should_display_error_message_when_error_exists() {
        // Given
        val stateWithError = ConverterUiState(
            error = "Connection error"
        )
        uiStateFlow.value = stateWithError

        // When
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Connection error").assertIsDisplayed()
    }

    @Test
    fun should_not_display_error_message_when_error_is_null() {
        // Given
        val stateWithoutError = ConverterUiState(
            error = null
        )
        uiStateFlow.value = stateWithoutError

        // When
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Connection error").assertDoesNotExist()
    }

    @Test
    fun should_call_swap_when_swap_button_clicked() {
        // Given
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // When
        composeTestRule.onNodeWithContentDescription("Swap currencies").performClick()

        // Then
        verify { mockViewModel.onSwap() }
    }

    @Test
    fun should_call_amount_change_when_sending_amount_changed() {
        // Given
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // When
        composeTestRule.onNodeWithText("100.00", useUnmergedTree = true)
            .performTextInput("200")

        // Then
        verify { mockViewModel.onFromAmountChanged("200") }
    }

    @Test
    fun should_call_amount_change_when_receiving_amount_changed() {
        // Given
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // When
        composeTestRule.onNodeWithText("0.0", useUnmergedTree = true)
            .performTextInput("300")

        // Then
        verify { mockViewModel.onToAmountChanged("300") }
    }

    @Test
    fun should_update_ui_when_state_changes() {
        // Given
        composeTestRule.setContent {
            ConverterScreen(viewModel = mockViewModel)
        }

        // When
        val newState = ConverterUiState(
            from = "GBP",
            to = "EUR",
            amountSending = "500.0",
            amountReceiving = "450.0",
            rate = 0.9
        )
        composeTestRule.runOnIdle {
            uiStateFlow.value = newState
        }

        // Then
        composeTestRule.onNodeWithText("GBP").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 GBP = 0.90 EUR").assertIsDisplayed()
    }
}