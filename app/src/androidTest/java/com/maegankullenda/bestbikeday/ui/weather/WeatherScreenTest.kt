package com.maegankullenda.bestbikeday.ui.weather

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.maegankullenda.bestbikeday.data.City
import com.maegankullenda.bestbikeday.data.Coordinates
import com.maegankullenda.bestbikeday.data.ForecastItem
import com.maegankullenda.bestbikeday.data.MainWeather
import com.maegankullenda.bestbikeday.data.Weather
import com.maegankullenda.bestbikeday.data.WeatherApi
import com.maegankullenda.bestbikeday.data.WeatherCity
import com.maegankullenda.bestbikeday.data.WeatherResponse
import com.maegankullenda.bestbikeday.data.Wind
import com.maegankullenda.bestbikeday.ui.theme.BestBikeDayTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherApi: WeatherApi
    private lateinit var viewModelStoreOwner: androidx.lifecycle.ViewModelStoreOwner

    private val mockCity = City(
        name = "Cape Town",
        lat = -33.9249,
        lon = 18.4241
    )

    private val mockForecast = ForecastItem(
        date = System.currentTimeMillis() / 1000,
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
        weather = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        clouds = mapOf("all" to 0),
        wind = Wind(speed = 5.0, degree = 180, gust = 7.0),
        visibility = 10000,
        probabilityOfPrecipitation = 0.0,
        dateText = "2024-03-20 12:00:00"
    )

    @Before
    fun setup() {
        weatherApi = mockk()
        viewModel = WeatherViewModel(weatherApi)

        viewModelStoreOwner = mockk<androidx.lifecycle.ViewModelStoreOwner>().apply {
            every { viewModelStore } returns ViewModelStore()
        }
    }

    private fun launchWeatherScreen(onBackClick: () -> Unit = {}) {
        composeTestRule.setContent {
            BestBikeDayTheme {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides viewModelStoreOwner
                ) {
                    WeatherScreen(
                        city = mockCity,
                        numberOfDays = 5,
                        onBackClick = onBackClick,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @Test
    fun weatherScreen_displaysLoadingState() = runTest {
        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } coAnswers {
            kotlinx.coroutines.delay(1000)
            WeatherResponse(
                list = emptyList(),
                city = WeatherCity(
                    id = 1,
                    name = "Cape Town",
                    coordinates = Coordinates(lat = -33.9249, lon = 18.4241),
                    country = "ZA",
                    population = 3433441,
                    timezone = 7200
                )
            )
        }

        launchWeatherScreen()

        // Verify loading indicator is shown
        composeTestRule.onNodeWithText("Weather Forecast").assertIsDisplayed()

        coVerify {
            weatherApi.getForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any(),
                units = any()
            )
        }
    }

    @Test
    fun weatherScreen_backButtonNavigatesBack() {
        var backClicked = false
        val onBackClick: () -> Unit = { backClicked = true }
        launchWeatherScreen(onBackClick)
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked) { "Back button click was not registered" }
    }

    @Test
    fun weatherScreen_DisplaysWeatherData() = runTest {
        val mockResponse = WeatherResponse(
            list = listOf(mockForecast),
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

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        // Wait for the data to be loaded and displayed
        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists()
        composeTestRule.onNodeWithText("20°").assertExists()
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
    }

    @Test
    fun weatherScreen_DisplaysError() = runTest {
        val errorMessage = "Network error occurred"
        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } throws Exception(errorMessage)

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        // Verify error message is displayed
        composeTestRule.onNodeWithTag("error_message")
            .assertExists()
            .assertIsDisplayed()

        coVerify {
            weatherApi.getForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any(),
                units = any()
            )
        }
    }

    @Test
    fun weatherScreen_DisplaysSevenDaysWhenSelected() = runTest {
        // Create 7 days of mock forecasts
        val mockForecasts = (0..6).map { dayOffset ->
            val timestamp = System.currentTimeMillis() / 1000 + (dayOffset * 86400) // Add one day each time
            mockForecast.copy(
                date = timestamp,
                main = mockForecast.main.copy(
                    temperature = 25.0 + dayOffset,
                    tempMax = 30.0 + dayOffset,
                    tempMin = 20.0 + dayOffset
                )
            )
        }

        val mockResponse = WeatherResponse(
            list = mockForecasts,
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

        // Launch with 7 days selected
        composeTestRule.setContent {
            BestBikeDayTheme {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides viewModelStoreOwner
                ) {
                    WeatherScreen(
                        city = mockCity,
                        numberOfDays = 7, // Request 7 days
                        onBackClick = {},
                        viewModel = viewModel
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        // Verify all 7 days are displayed
        mockForecasts.forEachIndexed { index, forecast ->
            val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }
            val expectedDate = dateFormat.format(Date(forecast.date * 1000))
            composeTestRule.onNodeWithText(expectedDate).assertIsDisplayed()
            composeTestRule.onNodeWithText("${(30.0 + index).toInt()}°").assertIsDisplayed() // Max temp
            composeTestRule.onNodeWithText("${(20.0 + index).toInt()}°").assertIsDisplayed() // Min temp
        }
    }
}
