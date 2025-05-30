package com.example.bestbikeday.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.bestbikeday.data.City
import com.example.bestbikeday.data.SouthAfricanCities
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysTitle() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, _ -> }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Weather Settings")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDefaultCity() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, _ -> }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText(SouthAfricanCities.cities.first().name)
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysViewWeatherButton() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, _ -> }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("View Weather Forecast")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_navigatesOnButtonClick() {
        var navigatedCity: City? = null
        var navigatedDays = 0

        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { city, days ->
                        navigatedCity = city
                        navigatedDays = days
                    }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("View Weather Forecast")
            .performClick()

        assert(navigatedCity == SouthAfricanCities.cities.first())
        assert(navigatedDays == 5) // Default value
    }
}
