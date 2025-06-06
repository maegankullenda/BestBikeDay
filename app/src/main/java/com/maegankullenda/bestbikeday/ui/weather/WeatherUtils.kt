package com.maegankullenda.bestbikeday.ui.weather

import androidx.compose.ui.graphics.Color

/**
 * Calculates a biking condition score from 0 (worst) to 100 (perfect)
 * based on weather parameters
 */
fun calculateBikingScore(temperature: Double, windSpeed: Double, weatherMain: String): Double {
    val tempScore = calculateTemperatureScore(temperature)
    val windScore = calculateWindScore(windSpeed)
    val weatherScore = calculateWeatherScore(weatherMain)

    return (tempScore + windScore + weatherScore) / 3.0
}

private fun calculateTemperatureScore(temperature: Double): Double {
    return when {
        temperature < 0 -> 30.0
        temperature < 10 -> 50.0 + (temperature * 3)
        temperature < 15 -> 80.0 + ((temperature - 10) * 4)
        temperature <= 25 -> 100.0
        temperature <= 30 -> 100.0 - ((temperature - 25) * 10)
        else -> 50.0
    }
}

private fun calculateWindScore(windSpeed: Double): Double {
    return when {
        windSpeed < 5 -> 100.0
        windSpeed < 15 -> 90.0
        windSpeed < 25 -> 70.0
        windSpeed < 35 -> 50.0
        else -> 30.0
    }
}

private fun calculateWeatherScore(weatherMain: String): Double {
    return when (weatherMain.lowercase()) {
        "clear" -> 100.0
        "clouds" -> 90.0
        "mist" -> 70.0
        "fog" -> 60.0
        "drizzle" -> 50.0
        "rain" -> 40.0
        "snow" -> 30.0
        "thunderstorm" -> 20.0
        else -> 50.0
    }
}

/**
 * Returns a color gradient based on the biking condition score
 */
fun getBikingConditionColor(score: Double): Color {
    return when {
        score >= 90 -> Color(0xFF4CAF50) // Green
        score >= 80 -> Color(0xFF8BC34A) // Light Green
        score >= 70 -> Color(0xFFCDDC39) // Lime
        score >= 60 -> Color(0xFFFFEB3B) // Yellow
        score >= 50 -> Color(0xFFFFC107) // Amber
        score >= 40 -> Color(0xFFFF9800) // Orange
        score >= 30 -> Color(0xFFFF5722) // Deep Orange
        else -> Color(0xFFF44336) // Red
    }
}
