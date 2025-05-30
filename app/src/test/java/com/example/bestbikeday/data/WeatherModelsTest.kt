package com.example.bestbikeday.data

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherModelsTest {

    @Test
    fun `test WeatherCity creation`() {
        val city = WeatherCity(
            name = "Cape Town",
            country = "ZA",
            coordinates = Coordinates(lat = -33.9249, lon = 18.4241)
        )

        assertEquals("Cape Town", city.name)
        assertEquals("ZA", city.country)
        assertEquals(-33.9249, city.coordinates.lat, 0.0001)
        assertEquals(18.4241, city.coordinates.lon, 0.0001)
    }

    @Test
    fun `test ForecastItem creation`() {
        val mainWeather = MainWeather(
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            humidity = 65
        )

        val weather = Weather(
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )

        val wind = Wind(speed = 5.0)

        val forecastItem = ForecastItem(
            date = 1234567890L,
            main = mainWeather,
            weather = listOf(weather),
            wind = wind
        )

        assertEquals(1234567890L, forecastItem.date)
        assertEquals(25.0, forecastItem.main.temp, 0.0001)
        assertEquals("Clear", forecastItem.weather.first().main)
        assertEquals(5.0, forecastItem.wind.speed, 0.0001)
    }

    @Test
    fun `test WeatherResponse creation`() {
        val city = WeatherCity(
            name = "Cape Town",
            country = "ZA",
            coordinates = Coordinates(lat = -33.9249, lon = 18.4241)
        )

        val forecastItem = ForecastItem(
            date = 1234567890L,
            main = MainWeather(25.0, 20.0, 30.0, 65),
            weather = listOf(Weather("Clear", "clear sky", "01d")),
            wind = Wind(5.0)
        )

        val response = WeatherResponse(
            list = listOf(forecastItem),
            city = city
        )

        assertEquals(1, response.list.size)
        assertEquals("Cape Town", response.city.name)
        assertEquals("clear sky", response.list.first().weather.first().description)
    }
}
