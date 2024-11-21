package com.example.weatherapplication

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

private const val TAG = "TOWM: WeatherViewModel"

class WeatherViewModel : ViewModel() {
    // Vars that are relevant to View:
    private val _weatherInfo = MutableLiveData<String>()
    val weatherInfo: LiveData<String>
        get() = _weatherInfo
    
    fun getWeatherInfo(context: Context, weatherUrl: String) {
        Log.d(TAG, "getWeatherInfo, weatherUrl=[$weatherUrl]")

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

            Log.v(TAG, "getWeatherInfo: temperature=[$temperature]")

            // Getting the city name
            val city = obj.getString("name")

            Log.v(TAG, "getWeatherInfo: city=[$city]")

            // set the temperature and the city
            // name using getString() function
            _weatherInfo.value = "$temperature degree Celsius in $city"
        },
            // In case of any error
            {
                _weatherInfo.value = "That didn't work! Check internet, api key, etc."

                Log.e(TAG, "getWeatherInfo: That didn't work! Check internet, api key, etc.")
            })
        
        queue.add(stringReq)
    }
}
