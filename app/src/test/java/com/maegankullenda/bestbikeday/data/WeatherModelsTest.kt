package com.maegankullenda.bestbikeday.data

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherModelsTest {

    @Test
    fun `test City creation`() {
        val coord = Coord(lat = -33.9249, lon = 18.4241)
        val city = City(
            id = 1,
            name = "Cape Town",
            coord = coord,
            country = "ZA",
            population = 3433441,
            timezone = 7200,
            sunrise = 1234567890L,
            sunset = 1234599999L
        )

        assertEquals("Cape Town", city.name)
        assertEquals("ZA", city.country)
        assertEquals(-33.9249, city.coord.lat, 0.0001)
        assertEquals(18.4241, city.coord.lon, 0.0001)
    }

    @Test
    fun `test WeatherData creation`() {
        val main = Main(
            temp = 25.0,
            feelsLike = 26.0,
            tempMin = 20.0,
            tempMax = 30.0,
            pressure = 1013,
            seaLevel = 1013,
            grndLevel = 1013,
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
            deg = 180,
            gust = 7.0
        )

        val clouds = Clouds(all = 0)
        val sys = Sys(pod = "d")

        val weatherData = WeatherData(
            dt = 1234567890L,
            main = main,
            weather = listOf(weather),
            clouds = clouds,
            wind = wind,
            visibility = 10000,
            pop = 0.0,
            sys = sys,
            dtTxt = "2024-03-20 12:00:00"
        )

        assertEquals(1234567890L, weatherData.dt)
        assertEquals(25.0, weatherData.main.temp, 0.0001)
        assertEquals("Clear", weatherData.weather.first().main)
        assertEquals(5.0, weatherData.wind.speed, 0.0001)
    }

    @Test
    fun `test WeatherResponse creation`() {
        val coord = Coord(lat = -33.9249, lon = 18.4241)
        val city = City(
            id = 1,
            name = "Cape Town",
            coord = coord,
            country = "ZA",
            population = 3433441,
            timezone = 7200,
            sunrise = 1234567890L,
            sunset = 1234599999L
        )

        val weatherData = WeatherData(
            dt = 1234567890L,
            main = Main(
                temp = 25.0,
                feelsLike = 26.0,
                tempMin = 20.0,
                tempMax = 30.0,
                pressure = 1013,
                seaLevel = 1013,
                grndLevel = 1013,
                humidity = 65,
                tempKf = 0.0
            ),
            weather = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            clouds = Clouds(all = 0),
            wind = Wind(speed = 5.0, deg = 180, gust = 7.0),
            visibility = 10000,
            pop = 0.0,
            sys = Sys(pod = "d"),
            dtTxt = "2024-03-20 12:00:00"
        )

        val response = WeatherResponse(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(weatherData),
            city = city
        )

        assertEquals(1, response.list.size)
        assertEquals("Cape Town", response.city.name)
        assertEquals("clear sky", response.list.first().weather.first().description)
    }
}
