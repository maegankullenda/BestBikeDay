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
        weatherApi = mockk<WeatherApi>()
        viewModel = WeatherViewModel()

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
        composeTestRule.waitForIdle()
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
            kotlinx.coroutines.delay(100)
            throw Exception("Simulated delay")
        }

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Weather Forecast")
            .assertIsDisplayed()
        coVerify(atLeast = 1) {
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
        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } returns WeatherResponse(
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

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists()
        composeTestRule.onNodeWithText("20°").assertExists()
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
        coVerify(atLeast = 1) {
            weatherApi.getForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any(),
                units = any()
            )
        }
    }

    @Test
    fun weatherScreen_DisplaysError() = runTest {
        val errorMessage = "Error message"
        coEvery {
            weatherApi.getForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } throws java.io.IOException(errorMessage)

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("error_message")
            .assertExists()

        coVerify(atLeast = 1) {
            weatherApi.getForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any(),
                units = any()
            )
        }
    }
}
