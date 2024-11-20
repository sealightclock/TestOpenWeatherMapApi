package com.example.weatherapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "TOWM: WeatherViewModel"

class WeatherViewModel : ViewModel() {
    private val _weatherInfo = MutableLiveData<String>()
    val weatherInfo: LiveData<String>
        get() = _weatherInfo

    fun getWeatherInfoFromWeb() {
        Log.d(TAG, "getWeatherInfoFromWeb")

        // Perform data fetching logic here
        _weatherInfo.value = "Weather info not yet available"
    }
}