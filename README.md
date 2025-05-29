# Best Bike Day

A weather forecast application for South African cities to help plan your perfect biking day.

## Features

- Weather forecast for major South African cities
- Customizable forecast duration (3, 5, or 7 days)
- Detailed weather information including temperature, wind speed, and conditions
- Beautiful Material Design 3 UI with a cohesive pink and blue color scheme

## Setup

1. Clone the repository
2. Copy `keystore.properties.template` to `keystore.properties`
3. Add your OpenWeatherMap API keys to `keystore.properties`:
   ```properties
   OPENWEATHER_API_KEY_DEBUG=your_debug_api_key_here
   OPENWEATHER_API_KEY_RELEASE=your_production_api_key_here
   ```
4. Build and run the project

## Technical Details

- Built with Kotlin and Jetpack Compose
- Uses Material Design 3 components
- Implements MVVM architecture
- Uses Retrofit for API communication
- Includes proper security measures for API key handling

## Requirements

- Android Studio Hedgehog or later
- Android SDK 24 or higher
- OpenWeatherMap API key

## License

This project is licensed under the MIT License - see the LICENSE file for details.
