package com.maegankullenda.bestbikeday.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Main response model for the OpenWeatherMap 5-day forecast API.
 * Contains a list of forecast items and city information.
 */
@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "list") val list: List<ForecastItem>,
    @Json(name = "city") val city: WeatherCity
)

/**
 * Represents city information from the weather API response.
 * Contains basic city details including name, country, and geographical coordinates.
 */
@JsonClass(generateAdapter = true)
data class WeatherCity(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "coord") val coordinates: Coordinates,
    @Json(name = "country") val country: String,
    @Json(name = "population") val population: Int,
    @Json(name = "timezone") val timezone: Int
)

/**
 * Geographic coordinates model containing latitude and longitude.
 * Used for specifying the location for weather data.
 */
@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double
)

/**
 * Represents a single weather forecast item for a specific time.
 * Contains main weather data, conditions, and wind information.
 * The date field is in Unix timestamp format.
 */
@JsonClass(generateAdapter = true)
data class ForecastItem(
    @Json(name = "dt") val date: Long,
    @Json(name = "main") val main: MainWeather,
    @Json(name = "weather") val weather: List<Weather>,
    @Json(name = "clouds") val clouds: Map<String, Int>,
    @Json(name = "wind") val wind: Wind,
    @Json(name = "visibility") val visibility: Int,
    @Json(name = "pop") val probabilityOfPrecipitation: Double,
    @Json(name = "dt_txt") val dateText: String
)

/**
 * Contains the main weather measurements for a forecast item.
 * Includes temperature data (current, min, max) and humidity.
 * All temperature values are in Celsius.
 */
@JsonClass(generateAdapter = true)
data class MainWeather(
    @Json(name = "temp") val temperature: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "sea_level") val seaLevel: Int,
    @Json(name = "grnd_level") val groundLevel: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "temp_kf") val tempKf: Double
)

/**
 * Describes weather conditions using standardized OpenWeatherMap parameters.
 * - main: General category (e.g., "Rain", "Clear", "Clouds")
 * - description: More detailed description of the weather
 * - icon: Icon code for weather visualization
 */
@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

/**
 * Contains wind-related weather data.
 * Speed is measured in meters per second.
 */
@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "speed") val speed: Double,
    @Json(name = "deg") val degree: Int,
    @Json(name = "gust") val gust: Double
)
