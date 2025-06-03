package com.maegankullenda.bestbikeday.data

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherModelsTest {

    @Test
    fun `test WeatherCity creation`() {
        val city = WeatherCity(
            id = 1,
            name = "Cape Town",
            country = "ZA",
            coordinates = Coordinates(lat = -33.9249, lon = 18.4241),
            population = 3433441,
            timezone = 7200
        )

        assertEquals("Cape Town", city.name)
        assertEquals("ZA", city.country)
        assertEquals(-33.9249, city.coordinates.lat, 0.0001)
        assertEquals(18.4241, city.coordinates.lon, 0.0001)
    }

    @Test
    fun `test ForecastItem creation`() {
        val mainWeather = MainWeather(
            temperature = 25.0,
            feelsLike = 26.0,
            tempMin = 20.0,
            tempMax = 30.0,
            pressure = 1013,
            seaLevel = 1013,
            groundLevel = 1013,
            humidity = 65,
            tempKf = 0.0
        )

        val weather = Weather(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )

        val wind = Wind(
            speed = 5.0,
            degree = 180,
            gust = 7.0
        )

        val forecastItem = ForecastItem(
            date = 1234567890L,
            main = mainWeather,
            weather = listOf(weather),
            clouds = mapOf("all" to 0),
            wind = wind,
            visibility = 10000,
            probabilityOfPrecipitation = 0.0,
            dateText = "2024-03-20 12:00:00"
        )

        assertEquals(1234567890L, forecastItem.date)
        assertEquals(25.0, forecastItem.main.temperature, 0.0001)
        assertEquals("Clear", forecastItem.weather.first().main)
        assertEquals(5.0, forecastItem.wind.speed, 0.0001)
    }

    @Test
    fun `test WeatherResponse creation`() {
        val city = WeatherCity(
            id = 1,
            name = "Cape Town",
            country = "ZA",
            coordinates = Coordinates(lat = -33.9249, lon = 18.4241),
            population = 3433441,
            timezone = 7200
        )

        val forecastItem = ForecastItem(
            date = 1234567890L,
            main = MainWeather(
                temperature = 25.0,
                feelsLike = 26.0,
                tempMin = 20.0,
                tempMax = 30.0,
                pressure = 1013,
                seaLevel = 1013,
                groundLevel = 1013,
                humidity = 65,
                tempKf = 0.0
            ),
            weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
            clouds = mapOf("all" to 0),
            wind = Wind(5.0, 180, 7.0),
            visibility = 10000,
            probabilityOfPrecipitation = 0.0,
            dateText = "2024-03-20 12:00:00"
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
