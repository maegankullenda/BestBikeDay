package com.maegankullenda.bestbikeday.ui.settings

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.maegankullenda.bestbikeday.data.SouthAfricanCities
import com.maegankullenda.bestbikeday.data.SouthAfricanCity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToWeather: (SouthAfricanCity, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf<SouthAfricanCity?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var numberOfDays by remember { mutableStateOf(5) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Video
        VideoPlayer(
            videoUri = "sports_background",
            modifier = Modifier.fillMaxSize(),
            isBackground = true
        )

        // Content with semi-transparent background for better readability
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = 0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select a City",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // City Dropdown using ExposedDropdownMenuBox
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCity?.name ?: "Select a city",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SouthAfricanCities.cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city.name) },
                                onClick = {
                                    selectedCity = city
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Number of Days Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Number of days:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (numberOfDays > 1) numberOfDays-- },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("-")
                    }
                    Text(
                        text = numberOfDays.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Button(
                        onClick = { if (numberOfDays < 5) numberOfDays++ },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        selectedCity?.let { city ->
                            onNavigateToWeather(city, numberOfDays)
                        }
                    },
                    enabled = selectedCity != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show Weather Forecast")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cycling Animation Video
                VideoPlayer(
                    videoUri = "cycling_animation",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    isBackground = false
                )
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    isBackground: Boolean = false
) {
    val context = LocalContext.current
    val tag = "VideoPlayer"

    Log.d(tag, "Initializing VideoPlayer with uri: $videoUri, isBackground: $isBackground")

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            try {
                val resourceId = context.resources.getIdentifier(
                    videoUri.removeSuffix(".mp4"),
                    "raw",
                    context.packageName
                )
                Log.d(tag, "Resource ID for $videoUri: $resourceId")

                if (resourceId != 0) {
                    val uri = Uri.parse("android.resource://${context.packageName}/$resourceId")
                    Log.d(tag, "Created URI: $uri")

                    val mediaItem = MediaItem.fromUri(uri)
                    setMediaItem(mediaItem)
                    repeatMode = ExoPlayer.REPEAT_MODE_ALL
                    playWhenReady = true
                    prepare()
                    Log.d(tag, "ExoPlayer prepared with mediaItem")
                } else {
                    Log.e(tag, "Failed to find resource ID for $videoUri")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error setting up ExoPlayer", e)
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(
        AndroidView(
            factory = { context ->
                Log.d(tag, "Creating PlayerView")
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    setShowNextButton(false)
                    setShowPreviousButton(false)

                    if (isBackground) {
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    } else {
                        resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }
            },
            modifier = modifier
        )
    ) {
        onDispose {
            Log.d(tag, "Disposing ExoPlayer")
            exoPlayer.release()
        }
    }
}
