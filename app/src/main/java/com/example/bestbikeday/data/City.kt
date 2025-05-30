package com.example.bestbikeday.data

data class City(
    val name: String,
    val lat: Double,
    val lon: Double
)

object SouthAfricanCities {
    val cities = listOf(
        City("Cape Town", -33.9249, 18.4241),
        City("Johannesburg", -26.2041, 28.0473),
        City("Durban", -29.8587, 31.0218),
        City("Pretoria", -25.7479, 28.2293),
        City("Port Elizabeth", -33.9608, 25.6022),
        City("Bloemfontein", -29.0852, 26.1596),
        City("Nelspruit", -25.4753, 30.9694),
        City("Kimberley", -28.7282, 24.7499),
        City("Polokwane", -23.9045, 29.4688),
        City("East London", -33.0153, 27.9116)
    )
}