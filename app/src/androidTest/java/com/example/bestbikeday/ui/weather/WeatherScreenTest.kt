package com.example.bestbikeday.ui.weather

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.bestbikeday.data.City
import com.example.bestbikeday.data.ForecastItem
import com.example.bestbikeday.data.MainWeather
import com.example.bestbikeday.data.Weather
import com.example.bestbikeday.data.WeatherApi
import com.example.bestbikeday.data.Wind
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
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
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            humidity = 65
        ),
        weather = listOf(
            Weather(
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        wind = Wind(speed = 5.0)
    )

    @Before
    fun setup() {
        weatherApi = mockk<WeatherApi>()
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
        composeTestRule.waitForIdle()
    }

    @Test
    fun weatherScreen_displaysLoadingState() = runTest {
        coEvery {
            weatherApi.getWeatherForecast(
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
            weatherApi.getWeatherForecast(
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
            weatherApi.getWeatherForecast(
                lat = any(),
                lon = any(),
                apiKey = any(),
                units = any()
            )
        } returns com.example.bestbikeday.data.WeatherResponse(
            list = listOf(mockForecast),
            city = com.example.bestbikeday.data.WeatherCity(
                name = "Cape Town",
                country = "ZA",
                coordinates = com.example.bestbikeday.data.Coordinates(
                    lat = -33.9249,
                    lon = 18.4241
                )
            )
        )

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists()
        composeTestRule.onNodeWithText("20°").assertExists()
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
        coVerify(atLeast = 1) {
            weatherApi.getWeatherForecast(
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
            weatherApi.getWeatherForecast(
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
            weatherApi.getWeatherForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any(),
                units = any()
            )
        }
    }
}
