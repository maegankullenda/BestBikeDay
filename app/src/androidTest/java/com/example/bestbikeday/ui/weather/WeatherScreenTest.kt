package com.example.bestbikeday.ui.weather

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var uiState: MutableStateFlow<WeatherUiState>
    private lateinit var factory: ViewModelProvider.Factory
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
        uiState = MutableStateFlow(WeatherUiState())
        // Create a proper mock of the ViewModel
        viewModel = mockk<WeatherViewModel>(relaxed = true).also {
            every { it.uiState } returns uiState
            coEvery { it.loadWeatherForecast(any(), any(), any()) } returns Unit
        }

        // Create a proper ViewModelStoreOwner mock
        viewModelStoreOwner = mockk<androidx.lifecycle.ViewModelStoreOwner>(relaxed = true).also {
            every { it.viewModelStore } returns ViewModelStore()
        }

        // Set up the factory mock
        factory = mockk<ViewModelProvider.Factory>().also {
            every { it.create(WeatherViewModel::class.java) } returns viewModel
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
    fun weatherScreen_displaysLoadingState() {
        uiState.value = WeatherUiState(isLoading = true)
        launchWeatherScreen()

        composeTestRule
            .onNodeWithText("Weather Forecast")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_backButtonNavigatesBack() {
        var backClicked = false
        launchWeatherScreen { backClicked = true }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun weatherScreen_DisplaysWeatherData() {
        uiState.value = WeatherUiState(
            isLoading = false,
            forecasts = listOf(mockForecast),
            cityName = "Cape Town"
        )

        launchWeatherScreen()

        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists()
        composeTestRule.onNodeWithText("20°").assertExists()
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
    }

    @Test
    fun weatherScreen_DisplaysError() {
        uiState.value = WeatherUiState(
            isLoading = false,
            error = "Network error occurred"
        )

        launchWeatherScreen()

        composeTestRule.onNodeWithText("Network error occurred").assertExists()
    }
}
