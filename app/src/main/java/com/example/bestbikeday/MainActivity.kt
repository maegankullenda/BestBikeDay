package com.example.bestbikeday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bestbikeday.data.City
import com.example.bestbikeday.ui.settings.SettingsScreen
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
import com.example.bestbikeday.ui.weather.WeatherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestBikeDayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var selectedCity by remember { mutableStateOf<City?>(null) }
                    var selectedDays by remember { mutableStateOf(5) }

                    if (selectedCity == null) {
                        SettingsScreen(
                            onNavigateToWeather = { city, days ->
                                selectedCity = city
                                selectedDays = days
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        WeatherScreen(
                            city = selectedCity!!,
                            numberOfDays = selectedDays,
                            onBackClick = { selectedCity = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
