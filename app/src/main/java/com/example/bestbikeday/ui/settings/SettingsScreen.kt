package com.example.bestbikeday.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bestbikeday.data.City
import com.example.bestbikeday.data.SouthAfricanCities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToWeather: (City, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf(SouthAfricanCities.cities.first()) }
    var expanded by remember { mutableStateOf(false) }
    var numberOfDays by remember { mutableStateOf(5) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1)) // Light Pink background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Weather Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // City Selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCity.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select City") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
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

        // Days Selector
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Number of days to display",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(3, 5, 7).forEach { days ->
                    FilterChip(
                        selected = numberOfDays == days,
                        onClick = { numberOfDays = days },
                        label = { Text("$days days") }
                    )
                }
            }
        }

        // View Weather Button
        Button(
            onClick = { onNavigateToWeather(selectedCity, numberOfDays) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("View Weather Forecast")
        }
    }
} 