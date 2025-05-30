package com.example.bestbikeday.ui.settings

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.bestbikeday.data.City
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_DisplaysAllElements() {
        // When
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Weather Settings").assertExists()
        composeTestRule.onNodeWithText("Select City").assertExists()
        composeTestRule.onNodeWithText("Number of days to display").assertExists()
        composeTestRule.onNodeWithText("3 days").assertExists()
        composeTestRule.onNodeWithText("5 days").assertExists()
        composeTestRule.onNodeWithText("7 days").assertExists()
        composeTestRule.onNodeWithText("View Weather Forecast").assertExists()
    }

    @Test
    fun settingsScreen_CanSelectCity() {
        var selectedCity: City? = null
        var selectedDays = 0

        // When
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { city, days ->
                        selectedCity = city
                        selectedDays = days
                    }
                )
            }
        }

        // Open dropdown and select Johannesburg
        composeTestRule.onNodeWithText("Select City").performClick()
        composeTestRule.onNodeWithText("Johannesburg").performClick()

        // Select 7 days
        composeTestRule.onNodeWithText("7 days").performClick()

        // Click view weather
        composeTestRule.onNodeWithText("View Weather Forecast").performClick()

        // Then - verify callback was called with correct values
        assert(selectedCity?.name == "Johannesburg")
        assert(selectedDays == 7)
    }

    @Test
    fun settingsScreen_DefaultValuesAreCorrect() {
        // When
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, _ -> }
                )
            }
        }

        // Then - verify default city is first in list (Cape Town)
        composeTestRule.onNodeWithText("Cape Town").assertExists()

        // Verify 5 days is selected by default
        composeTestRule.onNodeWithText("5 days")
            .assertIsSelected()
    }
}