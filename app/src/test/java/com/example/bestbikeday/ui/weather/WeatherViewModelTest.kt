package com.example.bestbikeday.ui.weather

import app.cash.turbine.test
import com.example.bestbikeday.data.Coordinates
import com.example.bestbikeday.data.ForecastItem
import com.example.bestbikeday.data.MainWeather
import com.example.bestbikeday.data.Weather
import com.example.bestbikeday.data.WeatherApi
import com.example.bestbikeday.data.WeatherCity
import com.example.bestbikeday.data.WeatherResponse
import com.example.bestbikeday.data.Wind
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherApi: WeatherApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherApi = mockk()
        viewModel = WeatherViewModel(weatherApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherForecast success updates state correctly`() = runTest {
        // Given
        val mockResponse = WeatherResponse(
            list = listOf(
                ForecastItem(
                    date = 1234567890L,
                    main = MainWeather(25.0, 20.0, 30.0, 65),
                    weather = listOf(Weather("Clear", "clear sky", "01d")),
                    wind = Wind(5.0)
                )
            ),
            city = WeatherCity("Cape Town", "ZA", Coordinates(-33.9249, 18.4241))
        )

        coEvery {
            weatherApi.getWeatherForecast(
                lat = any(),
                lon = any(),
                units = any(),
                apiKey = any()
            )
        } returns mockResponse

        // When & Then
        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.forecasts.isEmpty())
            assertNull(initial.error)

            viewModel.loadWeatherForecast(-33.9249, 18.4241, "test_api_key")

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val final = awaitItem()
            assertEquals("Cape Town", final.cityName)
            assertEquals(1, final.forecasts.size)
            assertEquals(25.0, final.forecasts[0].main.temp, 0.001)
            assertNull(final.error)
        }
    }

    @Test
    fun `loadWeatherForecast network error updates state correctly`() = runTest {
        // Given
        coEvery {
            weatherApi.getWeatherForecast(
                lat = any(),
                lon = any(),
                units = any(),
                apiKey = any()
            )
        } throws IOException("Network error")

        // When & Then
        viewModel.uiState.test {
            val initial = awaitItem()
            assertTrue(initial.forecasts.isEmpty())

            viewModel.loadWeatherForecast(-33.9249, 18.4241, "test_api_key")

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val error = awaitItem()
            assertTrue(error.error?.contains("Network error") == true)
            assertTrue(error.forecasts.isEmpty())
        }
    }
}
