package com.maegankullenda.bestbikeday.ui.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoPlayerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun videoPlayer_backgroundVideo_displaysCorrectly() {
        composeTestRule.setContent {
            VideoPlayer(
                videoUri = "sports_background",
                isBackground = true
            )
        }
        composeTestRule.onNodeWithContentDescription("Video Player").assertExists()
    }

    @Test
    fun videoPlayer_cyclingAnimation_displaysCorrectly() {
        composeTestRule.setContent {
            VideoPlayer(
                videoUri = "cycling_animation",
                isBackground = false
            )
        }
        composeTestRule.onNodeWithContentDescription("Video Player").assertExists()
    }
}
