package com.maegankullenda.bestbikeday.ui.weather

import com.maegankullenda.bestbikeday.data.City
import com.maegankullenda.bestbikeday.data.Clouds
import com.maegankullenda.bestbikeday.data.Coord
import com.maegankullenda.bestbikeday.data.Main
import com.maegankullenda.bestbikeday.data.Sys
import com.maegankullenda.bestbikeday.data.Weather
import com.maegankullenda.bestbikeday.data.WeatherData
import com.maegankullenda.bestbikeday.data.WeatherResponse
import com.maegankullenda.bestbikeday.data.WeatherService
import com.maegankullenda.bestbikeday.data.Wind
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
    private lateinit var weatherService: WeatherService
    private lateinit var viewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherService = mockk()
        viewModel = WeatherViewModel(weatherService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherForecast returns correct number of daily forecasts`() = runTest {
        // Create test data with multiple forecasts per day
        val today = LocalDateTime.now()
        val forecasts = (0..4).flatMap { dayOffset ->
            (0..23 step 3).map { hour ->
                val timestamp = today.plusDays(dayOffset.toLong())
                    .withHour(hour)
                    .toEpochSecond(ZoneOffset.UTC)

                WeatherData(
                    dt = timestamp,
                    main = Main(
                        temp = 20.0 + dayOffset,
                        feelsLike = 22.0,
                        tempMin = 18.0,
                        tempMax = 25.0,
                        pressure = 1013,
                        seaLevel = 1013,
                        grndLevel = 1013,
                        humidity = 65,
                        tempKf = 0.0
                    ),
                    weather = listOf(
                        Weather(
                            id = 800,
                            main = "Clear",
                            description = "clear sky",
                            icon = "01d"
                        )
                    ),
                    clouds = Clouds(all = 0),
                    wind = Wind(speed = 5.0, deg = 180, gust = 7.0),
                    visibility = 10000,
                    pop = 0.0,
                    sys = Sys(pod = "d"),
                    dtTxt = timestamp.toString()
                )
            }
        }

        val response = WeatherResponse(
            cod = "200",
            message = 0,
            cnt = forecasts.size,
            list = forecasts,
            city = City(
                id = 1,
                name = "Test City",
                coord = Coord(lat = -33.9249, lon = 18.4241),
                country = "ZA",
                population = 1000000,
                timezone = 7200,
                sunrise = today.toEpochSecond(ZoneOffset.UTC),
                sunset = today.plusHours(12).toEpochSecond(ZoneOffset.UTC)
            )
        )

        coEvery {
            weatherService.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } returns response

        // Load forecasts
        viewModel.loadWeatherForecast(
            lat = -33.9249,
            lon = 18.4241,
            apiKey = "test_key"
        )

        // Wait for the coroutine to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify results
        val state = viewModel.uiState.value
        Assert.assertNotNull("Forecast should not be null", state.forecast)
        Assert.assertEquals("Should return exactly 5 daily forecasts", 5, state.forecast.size)

        // Verify each forecast is for a different day
        val uniqueDays = state.forecast.map { forecast ->
            java.time.Instant.ofEpochSecond(forecast.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .toString()
        }.toSet()

        Assert.assertEquals("Each forecast should be for a different day", 5, uniqueDays.size)

        // Verify forecasts are around noon
        state.forecast.forEach { forecast ->
            val hour = java.time.Instant.ofEpochSecond(forecast.date)
                .atZone(java.time.ZoneId.systemDefault())
                .hour
            Assert.assertTrue(
                "Forecast hour should be close to noon, was $hour",
                hour in 11..13
            )
        }
    }
}
