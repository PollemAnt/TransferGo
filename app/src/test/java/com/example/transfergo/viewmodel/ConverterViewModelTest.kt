package com.example.transfergo.viewmodel

import com.example.transfergo.data.repository.Repository
import com.example.transfergo.ui.converter.ConverterViewModel
import com.example.transfergo.util.FxResult
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ConverterViewModelTest {

    @Mock
    private lateinit var mockRepository: Repository

    private lateinit var viewModel: ConverterViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ConverterViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onFromAmountChanged should update amountSending`() {
        // Given & When
        viewModel.onFromAmountChanged("100.50")

        // Then
        TestCase.assertEquals("100.50", viewModel.uiState.value.amountSending)
    }

    @Test
    fun `onToAmountChanged should update amountReceiving`() {
        // Given & When
        viewModel.onToAmountChanged("200.75")

        // Then
        TestCase.assertEquals("200.75", viewModel.uiState.value.amountReceiving)
    }

    @Test
    fun `onFromAmountChanged should normalize commas to dots`() {
        // Given & When
        viewModel.onFromAmountChanged("100,50")

        // Then
        TestCase.assertEquals("100.50", viewModel.uiState.value.amountSending)
    }

    @Test
    fun `onSwap should swap currencies and amounts`() = runTest {
        // Given
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Success(0.23 to 23.0))

        viewModel.onFromAmountChanged("100.00")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify initial state
        assertEquals("100.00", viewModel.uiState.value.amountSending)
        assertEquals("23.00", viewModel.uiState.value.amountReceiving)
        assertEquals("PLN", viewModel.uiState.value.from)
        assertEquals("UAH", viewModel.uiState.value.to)

        // When
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Success(1.5 to 150.00))

        viewModel.onSwap()
        testDispatcher.scheduler.advanceUntilIdle()


        // Then
        assertEquals("UAH", viewModel.uiState.value.from)
        assertEquals("PLN", viewModel.uiState.value.to)
        assertEquals("23.00", viewModel.uiState.value.amountSending)
        assertEquals("150.00", viewModel.uiState.value.amountReceiving)
    }

    @Test
    fun `onFromCurrencySelected should update from and to currency`()= runTest {
        // Given
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Success(1.0 to 100.0))

        // When
        viewModel.onFromCurrencySelected("EUR", "EUR")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should swap currencies
        assertEquals("EUR", viewModel.uiState.value.from)
        assertEquals("PLN", viewModel.uiState.value.to)
    }

    @Test
    fun `onFromCurrencySelected should swap currencies when same as to currency`()= runTest {
        // Given
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Success(1.0 to 100.0))

        viewModel.onToCurrencySelected("EUR","PLN")

        // When
        viewModel.onFromCurrencySelected("EUR","EUR")

        testDispatcher.scheduler.advanceUntilIdle()
        // Then
        TestCase.assertEquals("EUR", viewModel.uiState.value.from)
        TestCase.assertEquals("PLN", viewModel.uiState.value.to)
    }

    @Test
    fun `conversion should update amountReceiving when from amount changes`() =
        runTest(testDispatcher) {
            // Given
            whenever(mockRepository.convert(any(), any(), any()))
                .thenReturn(FxResult.Success(Pair(4.0, 400.0)))

            // When
            viewModel.onFromAmountChanged("100.0")
            advanceUntilIdle()

            // Then
            TestCase.assertEquals("400.00", viewModel.uiState.value.amountReceiving)
            TestCase.assertEquals(4.0, viewModel.uiState.value.rate)
        }

    @Test
    fun `should show error when conversion fails`() = runTest(testDispatcher) {
        // Given
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Error("API Error"))

        // When
        viewModel.onFromAmountChanged("100.0")
        advanceUntilIdle()

        // Then
        TestCase.assertNotNull(viewModel.uiState.value.error)
        TestCase.assertEquals("API Error", viewModel.uiState.value.error)
    }

    @Test
    fun `should clear error on successful conversion`() = runTest(testDispatcher) {
        // Given
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Error("API Error"))
        viewModel.onFromAmountChanged("100.0")
        advanceUntilIdle()
        TestCase.assertNotNull(viewModel.uiState.value.error)

        // When
        whenever(mockRepository.convert(any(), any(), any()))
            .thenReturn(FxResult.Success(Pair(4.0, 400.0)))
        viewModel.onFromAmountChanged("50.0")
        advanceUntilIdle()

        // Then
        TestCase.assertNull(viewModel.uiState.value.error)
    }
}