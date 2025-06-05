package com.maegankullenda.bestbikeday.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.maegankullenda.bestbikeday.data.City
import com.maegankullenda.bestbikeday.data.SouthAfricanCities
import com.maegankullenda.bestbikeday.ui.theme.BestBikeDayTheme
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
            .onNodeWithText("Select a City")
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
            .onNodeWithText("Select a city")
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
            .onNodeWithText("Show Weather Forecast")
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

        // Select a city first
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Select a city")
            .performClick()

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText(SouthAfricanCities.cities.first().name)
            .performClick()

        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithText("Show Weather Forecast")
            .performClick()

        assert(navigatedCity == SouthAfricanCities.cities.first())
        assert(navigatedDays == 5) // Default value
    }

    @Test
    fun settingsScreen_allowsUpToSevenDays() {
        var navigatedDays = 0

        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onNavigateToWeather = { _, days ->
                        navigatedDays = days
                    }
                )
            }
        }

        // Click the + button twice to increase from default 5 to 7
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("+").performClick()

        // Select a city to enable navigation
        composeTestRule.onNodeWithText("Select a city").performClick()
        composeTestRule.onNodeWithText(SouthAfricanCities.cities.first().name).performClick()

        // Click the weather forecast button
        composeTestRule.onNodeWithText("Show Weather Forecast").performClick()

        assert(navigatedDays == 7) // Should allow up to 7 days
    }
}
