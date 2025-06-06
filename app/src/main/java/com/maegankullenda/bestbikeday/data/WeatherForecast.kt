package com.maegankullenda.bestbikeday.data

data class WeatherForecast(
    val date: Long,
    val temperature: Double,
    val windSpeed: Double,
    val weatherMain: String,
    val weatherDescription: String
)
