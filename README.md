# TestOpenWeatherMapApi

Kotlin
xml layouts (tried transitioning to Jetpack Compose)
libs.version.toml
build.gradle.kts

OpenWeatherMap API
Volley for RESTful API

ViewModel.kt:
private val _weatherInfo = MutableLiveData<String>()
val weatherInfo: LiveData<String>
get() = _weatherInfo

MainActivity.kt:
weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
data/view binding