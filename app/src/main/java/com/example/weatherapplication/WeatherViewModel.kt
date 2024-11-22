package com.example.weatherapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

private const val TAG = "TOWM: WeatherViewModel"

class WeatherViewModel : ViewModel() {
    // Vars that are relevant to View:
    private val _weatherInfo = MutableLiveData<String>()
    val weatherInfo: LiveData<String>
        get() = _weatherInfo

    // weather url to get JSON
    private var weatherUrl = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // api id for url from https://home.openweathermap.org/api_keys for Jonathan Zhong:
    private var apiKey = "cc9a943e9b0082101297ca40b03f1f83"

    @SuppressLint("MissingPermission")
    fun obtainLocation(context: Context, activity: Activity) {
        Log.d(TAG, "obtainLocation")

        // create an instance of the Fused
        // Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        // get the last location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.v(TAG, "obtainLocation: addOnSuccessListener: location=[${location.latitude}, ${location.longitude}]")
                }

                // get the latitude and longitude
                // and create the http URL
                if (location != null) {
                    // Use this test case with hard-coded city name first to see how the code works:
                    //api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=cc9a943e9b0082101297ca40b03f1f83
                    //weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=cc9a943e9b0082101297ca40b03f1f83"
                    //weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=Irvine,USA&APPID=cc9a943e9b0082101297ca40b03f1f83"

                    // Then use this code:
                    weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&units=metric&APPID=${apiKey}"

                    Log.v(TAG, "obtainLocation: addOnSuccessListener: weatherUrl=[$weatherUrl]")
                }
                // this function will
                // fetch data from URL
                getWeatherInfoApi(context, weatherUrl)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Location Permission not granted", Toast.LENGTH_SHORT).show()

                Log.e(TAG, "obtainLocation: addOnSuccessListener: stackTrace=\n${exception.printStackTrace()}")
            }
    }

    private fun getWeatherInfoApi(context: Context, weatherUrl: String) {
        Log.d(TAG, "getWeatherInfoApi: weatherUrl=[$weatherUrl]")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url: String = weatherUrl

        // Request a string response
        // from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url, { response ->
            // get the JSON object
            val obj = JSONObject(response)

            // Getting the temperature readings from response
            val main: JSONObject = obj.getJSONObject("main")
            val temperature = main.getString("temp")

            Log.v(TAG, "getWeatherInfoApi: temperature=[$temperature]")

            // Getting the city name
            val city = obj.getString("name")

            Log.v(TAG, "getWeatherInfoApi: city=[$city]")

            // set the temperature and the city
            // name using getString() function
            _weatherInfo.value = "$temperature degree Celsius in $city"
        },
            // In case of any error
            {
                _weatherInfo.value = "That didn't work! Check internet, api key, etc."

                Log.e(TAG, "getWeatherInfoApi: That didn't work! Check internet, api key, etc.")
            })
        
        queue.add(stringReq)
    }
}
