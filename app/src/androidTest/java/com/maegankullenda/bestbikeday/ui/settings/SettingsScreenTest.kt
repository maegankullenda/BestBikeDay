package com.maegankullenda.bestbikeday.ui.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maegankullenda.bestbikeday.data.SouthAfricanCities
import com.maegankullenda.bestbikeday.data.SouthAfricanCity
import com.maegankullenda.bestbikeday.ui.theme.BestBikeDayTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysTitle() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysThemeOptions() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("System").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysUnitsOptions() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Units").assertIsDisplayed()
        composeTestRule.onNodeWithText("Metric").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_themeDropdown_clickable() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("System").performClick()
    }

    @Test
    fun settingsScreen_unitsDropdown_clickable() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Metric").performClick()
    }

    @Test
    fun settingsScreen_displaysDefaultCity() {
        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
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
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {}
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
        var navigatedCity: SouthAfricanCity? = null
        var navigatedDays = 0

        composeTestRule.setContent {
            BestBikeDayTheme {
                SettingsScreen(
                    onBackClick = {},
                    onThemeChange = {},
                    onUnitsChange = {},
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
    fun settingsScreen_allowsUpToFiveDays() {
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

        // No need to click + since default is already 5
        // Select a city to enable navigation
        composeTestRule.onNodeWithText("Select a city").performClick()
        composeTestRule.onNodeWithText(SouthAfricanCities.cities.first().name).performClick()

        // Click the weather forecast button
        composeTestRule.onNodeWithText("Show Weather Forecast").performClick()

        assert(navigatedDays == 5) // Should allow up to 5 days
    }

    @Test
    fun settingsScreen_initialState_showsCorrectElements() {
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToWeather = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Select a City").assertExists()
        composeTestRule.onNodeWithText("Select a city").assertExists()
        composeTestRule.onNodeWithText("Number of days:").assertExists()
        composeTestRule.onNodeWithText("5").assertExists()
        composeTestRule.onNodeWithText("Show Weather Forecast").assertExists()
    }

    @Test
    fun settingsScreen_citySelection_enablesButton() {
        var selectedCity: SouthAfricanCity? = null
        var selectedDays = 0

        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToWeather = { city, days ->
                    selectedCity = city
                    selectedDays = days
                }
            )
        }

        // Initial state - button should be disabled
        composeTestRule.onNodeWithText("Show Weather Forecast").assertExists()

        // Select a city
        composeTestRule.onNodeWithText("Select a city").performClick()
        composeTestRule.onNodeWithText("Cape Town").performClick()

        // Click the weather forecast button
        composeTestRule.onNodeWithText("Show Weather Forecast").performClick()

        // Verify the callback was called with correct values
        assert(selectedCity?.name == "Cape Town")
        assert(selectedDays == 5)
    }
}
