package com.example.bestbikeday.ui.weather

import androidx.compose.ui.test.assertExists
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
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class WeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCity = City(
        name = "Cape Town",
        lat = -33.9249,
        lon = 18.4241
    )

    private val mockForecast = ForecastItem(
        date = System.currentTimeMillis(),
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

    @Test
    fun weatherScreen_DisplaysLoadingState() {
        // Given
        val viewModel = mockk<WeatherViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            WeatherUiState(isLoading = true)
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
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun weatherScreen_DisplaysWeatherData() {
        // Given
        val viewModel = mockk<WeatherViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            WeatherUiState(
                isLoading = false,
                forecasts = listOf(mockForecast),
                cityName = "Cape Town"
            )
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
        val viewModel = mockk<WeatherViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            WeatherUiState(
                isLoading = false,
                error = "Network error occurred"
            )
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

    @Test
    fun weatherScreen_BackButtonWorks() {
        // Given
        var backClicked = false
        val viewModel = mockk<WeatherViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(WeatherUiState())

        // When
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

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }
}