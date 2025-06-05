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

    @Test
    fun `loadWeatherForecast groups forecasts by day correctly`() = runTest {
        // Given - Create multiple forecasts for the same day at different times
        val day1Time1 = 1709596800L // March 5, 2024 00:00:00 UTC
        val day1Time2 = 1709607600L // March 5, 2024 03:00:00 UTC
        val day1Time3 = 1709618400L // March 5, 2024 06:00:00 UTC
        val day2Time1 = 1709683200L // March 6, 2024 00:00:00 UTC
        val day2Time2 = 1709694000L // March 6, 2024 03:00:00 UTC

        val mockForecasts = listOf(
            createMockForecast(day1Time1, 20.0, 18.0),
            createMockForecast(day1Time2, 22.0, 19.0),
            createMockForecast(day1Time3, 25.0, 20.0),
            createMockForecast(day2Time1, 21.0, 19.0),
            createMockForecast(day2Time2, 23.0, 20.0)
        )

        val mockResponse = WeatherResponse(
            list = mockForecasts,
            city = WeatherCity(
                id = 1,
                name = "Test City",
                coordinates = Coordinates(lat = 0.0, lon = 0.0),
                country = "TC",
                population = 100000,
                timezone = 0
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

            viewModel.loadWeatherForecast(0.0, 0.0, "test_api_key")

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val final = awaitItem()
            assertEquals("Should have exactly 2 days of forecasts", 2, final.forecasts.size)

            // Verify first day's forecast (middle of 3 forecasts)
            val day1Forecast = final.forecasts[0]
            assertEquals("Should pick middle forecast for day 1", day1Time2, day1Forecast.date)
            assertEquals(22.0, day1Forecast.main.temperature, 0.1)

            // Verify second day's forecast (middle of 2 forecasts)
            val day2Forecast = final.forecasts[1]
            assertEquals("Should pick middle forecast for day 2", day2Time2, day2Forecast.date)
            assertEquals(23.0, day2Forecast.main.temperature, 0.1)
        }
    }

    @Test
    fun `loadWeatherForecast limits to 5 days maximum`() = runTest {
        // Given - Create forecasts for 6 different days
        val mockForecasts = (0..5).map { dayOffset ->
            val timestamp = 1709596800L + (dayOffset * 86400) // Add one day each time
            createMockForecast(timestamp, 20.0 + dayOffset, 18.0 + dayOffset)
        }

        val mockResponse = WeatherResponse(
            list = mockForecasts,
            city = WeatherCity(
                id = 1,
                name = "Test City",
                coordinates = Coordinates(lat = 0.0, lon = 0.0),
                country = "TC",
                population = 100000,
                timezone = 0
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

            viewModel.loadWeatherForecast(0.0, 0.0, "test_api_key")

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val final = awaitItem()
            assertEquals("Should limit to 5 days maximum", 5, final.forecasts.size)

            // Verify the temperatures increase by 1 degree each day
            for (i in 0..4) {
                assertEquals(20.0 + i, final.forecasts[i].main.temperature, 0.1)
            }
        }
    }

    private fun createMockForecast(timestamp: Long, temp: Double, minTemp: Double): ForecastItem {
        return ForecastItem(
            date = timestamp,
            main = MainWeather(
                temperature = temp,
                feelsLike = temp + 1,
                tempMin = minTemp,
                tempMax = temp + 2,
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
            dateText = "2024-03-05 12:00:00"
        )
    }
}
