package com.maegankullenda.bestbikeday.ui.weather

import app.cash.turbine.test
import com.maegankullenda.bestbikeday.data.Coordinates
import com.maegankullenda.bestbikeday.data.ForecastItem
import com.maegankullenda.bestbikeday.data.MainWeather
import com.maegankullenda.bestbikeday.data.Weather
import com.maegankullenda.bestbikeday.data.WeatherApi
import com.maegankullenda.bestbikeday.data.WeatherCity
import com.maegankullenda.bestbikeday.data.WeatherResponse
import com.maegankullenda.bestbikeday.data.Wind
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
                    main = MainWeather(
                        temperature = 25.0,
                        feelsLike = 26.0,
                        tempMin = 20.0,
                        tempMax = 30.0,
                        pressure = 1013,
                        seaLevel = 1013,
                        groundLevel = 1013,
                        humidity = 65,
                        tempKf = 0.0
                    ),
                    weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
                    clouds = mapOf("all" to 0),
                    wind = Wind(5.0, 180, 7.0),
                    visibility = 10000,
                    probabilityOfPrecipitation = 0.0,
                    dateText = "2024-03-20 12:00:00"
                )
            ),
            city = WeatherCity(
                id = 1,
                name = "Cape Town",
                coordinates = Coordinates(lat = -33.9249, lon = 18.4241),
                country = "ZA",
                population = 3433441,
                timezone = 7200
            )
        )

        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
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
            assertEquals(25.0, final.forecasts[0].main.temperature, 0.001)
            assertNull(final.error)
        }
    }

    @Test
    fun `loadWeatherForecast network error updates state correctly`() = runTest {
        // Given
        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
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
