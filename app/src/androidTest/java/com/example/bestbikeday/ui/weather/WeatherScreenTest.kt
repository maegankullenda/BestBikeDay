package com.example.bestbikeday.ui.weather

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.bestbikeday.data.City
import com.example.bestbikeday.data.ForecastItem
import com.example.bestbikeday.data.MainWeather
import com.example.bestbikeday.data.Weather
import com.example.bestbikeday.data.Wind
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var uiState: MutableStateFlow<WeatherUiState>
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
        uiState = spyk(MutableStateFlow(WeatherUiState()))
        viewModel = mockk<WeatherViewModel>(relaxed = true).apply {
            every { uiState } returns uiState
            coEvery {
                loadWeatherForecast(
                    lat = any(),
                    lon = any(),
                    apiKey = any()
                )
            } returns Unit
        }

        viewModelStoreOwner = mockk<androidx.lifecycle.ViewModelStoreOwner>(relaxed = true).apply {
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
    fun weatherScreen_displaysLoadingState() {
        uiState.value = WeatherUiState(isLoading = true)
        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Weather Forecast")
            .assertIsDisplayed()
        verify(atLeast = 1) { viewModel.uiState }
        verify(atLeast = 1) {
            viewModel.loadWeatherForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any()
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
        verify(atLeast = 1) { viewModel.uiState }
    }

    @Test
    fun weatherScreen_DisplaysWeatherData() {
        uiState.value = WeatherUiState(
            isLoading = false,
            forecasts = listOf(mockForecast),
            cityName = "Cape Town"
        )

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists()
        composeTestRule.onNodeWithText("20°").assertExists()
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
        verify(atLeast = 1) { viewModel.uiState }
        verify(atLeast = 1) {
            viewModel.loadWeatherForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any()
            )
        }
    }

    @Test
    fun weatherScreen_DisplaysError() {
        uiState.value = WeatherUiState(
            isLoading = false,
            error = "Network error occurred"
        )

        launchWeatherScreen()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Network error occurred").assertExists()
        verify(atLeast = 1) { viewModel.uiState }
        verify(atLeast = 1) {
            viewModel.loadWeatherForecast(
                lat = mockCity.lat,
                lon = mockCity.lon,
                apiKey = any()
            )
        }
    }
}
