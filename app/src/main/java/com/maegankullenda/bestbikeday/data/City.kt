package com.maegankullenda.bestbikeday.data

data class SouthAfricanCity(
    val name: String,
    val lat: Double,
    val lon: Double
)

object SouthAfricanCities {
    val cities = listOf(
        SouthAfricanCity("Johannesburg", -26.2041, 28.0473),
        SouthAfricanCity("Cape Town", -33.9249, 18.4241),
        SouthAfricanCity("Durban", -29.8587, 31.0218),
        SouthAfricanCity("Pretoria", -25.7479, 28.2293),
        SouthAfricanCity("Port Elizabeth", -33.9608, 25.6022),
        SouthAfricanCity("Bloemfontein", -29.0852, 26.1596),
        SouthAfricanCity("Nelspruit", -25.4753, 30.9694),
        SouthAfricanCity("Kimberley", -28.7282, 24.7499),
        SouthAfricanCity("Polokwane", -23.9045, 29.4688),
        SouthAfricanCity("East London", -32.9783, 27.8645)
    )
}
