package com.example.bestbikeday.ui.weather

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var uiState: MutableStateFlow<WeatherUiState>

    private val mockCity = City(
        name = "Cape Town",
        lat = -33.9249,
        lon = 18.4241
    )

    private val mockForecast = ForecastItem(
        date = System.currentTimeMillis() / 1000, // Convert to seconds
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
        viewModel = mockk(relaxed = true) {
            every { uiState } returns uiState
            coEvery { 
                loadWeatherForecast(
                    lat = any(),
                    lon = any(),
                    apiKey = any()
                )
            } returns Unit
        }
    }

    @Test
    fun weatherScreen_displaysLoadingState() {
        uiState.value = WeatherUiState(isLoading = true)

        composeTestRule.setContent {
            BestBikeDayTheme {
                WeatherScreen(
                    city = mockCity,
                    numberOfDays = 5,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText("Weather Forecast")
            .assertIsDisplayed()
    }

    @Test
    fun weatherScreen_backButtonNavigatesBack() {
        var backClicked = false

        composeTestRule.setContent {
            BestBikeDayTheme {
                WeatherScreen(
                    city = mockCity,
                    numberOfDays = 5,
                    onBackClick = { backClicked = true },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun weatherScreen_DisplaysWeatherData() {
        // Given
        uiState.value = WeatherUiState(
            isLoading = false,
            forecasts = listOf(mockForecast),
            cityName = "Cape Town"
        )

        // When
        composeTestRule.setContent {
            BestBikeDayTheme {
                WeatherScreen(
                    city = mockCity,
                    numberOfDays = 5,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Weather Forecast for Cape Town").assertExists()
        composeTestRule.onNodeWithText("30°").assertExists() // Max temp
        composeTestRule.onNodeWithText("20°").assertExists() // Min temp
        composeTestRule.onNodeWithText("Wind: 5 km/h").assertExists()
    }

    @Test
    fun weatherScreen_DisplaysError() {
        // Given
        uiState.value = WeatherUiState(
            isLoading = false,
            error = "Network error occurred"
        )

        // When
        composeTestRule.setContent {
            BestBikeDayTheme {
                WeatherScreen(
                    city = mockCity,
                    numberOfDays = 5,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Network error occurred").assertExists()
    }
}
