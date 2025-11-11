package com.example.transfergo.data

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.data.repository.FxRepository
import com.example.transfergo.data.repository.FxResponse
import com.example.transfergo.util.FxResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class FxRepositoryTest {

    @Mock
    private lateinit var mockFxApi: FxApi

    private lateinit var repository: FxRepository

    private companion object {
        const val FROM_CURRENCY = "PLN"
        const val TO_CURRENCY = "EUR"
        const val AMOUNT = 100.0
    }

    @Before
    fun setUp() {
        repository = FxRepository(mockFxApi)
    }

    @Test
    fun `convert should return success on valid API response`() = runTest {

        val expectedRate = 0.23
        val expectedConvertedAmount = 23.0


        val mockResponse = FxResponse(
            rate = expectedRate,
            from = FROM_CURRENCY,
            to = TO_CURRENCY,
            fromAmount = AMOUNT,
            toAmount = expectedConvertedAmount
        )

        whenever(mockFxApi.getFxRate(FROM_CURRENCY, TO_CURRENCY, AMOUNT))
            .thenReturn(mockResponse)

        // When
        val result = repository.convert(FROM_CURRENCY, TO_CURRENCY, AMOUNT)

        // Then
        assertIs<FxResult.Success<Pair<Double, Double>>>(result)

        assertEquals(expectedRate, result.data.first)
        assertEquals(expectedConvertedAmount, result.data.second)
    }

    @Test
    fun `convert should return network error when API throws connection exception`() = runTest {
        whenever(mockFxApi.getFxRate(FROM_CURRENCY, TO_CURRENCY, AMOUNT))
            .thenAnswer { throw IOException("Test")}

        val result = repository.convert(FROM_CURRENCY, TO_CURRENCY, AMOUNT)

        assertIs<FxResult.Error>(result)
        assertEquals("Network error: Test", result.error)
    }

    @Test
    fun `convert should return error when API throws exception`() = runTest {
        val exception = RuntimeException("Test")

        whenever(mockFxApi.getFxRate(FROM_CURRENCY, TO_CURRENCY, AMOUNT))
            .thenThrow(exception)

        val result = repository.convert(FROM_CURRENCY, TO_CURRENCY, AMOUNT)

        assertIs<FxResult.Error>(result)
        assertEquals("Conversion error: Test", result.error)
    }
}
