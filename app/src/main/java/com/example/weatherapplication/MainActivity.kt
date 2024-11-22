package com.example.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private const val TAG = "TOWM: MainActivity"

class MainActivity : AppCompatActivity() {
    // weather url to get JSON
    private var weatherUrl = ""

    // api id for url from https://home.openweathermap.org/api_keys for Jonathan Zhong:
    private var apiKey = "cc9a943e9b0082101297ca40b03f1f83"

    private lateinit var GetWeatherButton : Button
    private lateinit var weatherInfoTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // link the textView in which the
        // temperature will be displayed
        weatherInfoTextView = findViewById(R.id.weather_info_text_view)

        GetWeatherButton = findViewById(R.id.get_weather_button)

        // create an instance of the Fused
        // Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // on clicking this button function to
        // get the coordinates will be called
        GetWeatherButton.setOnClickListener {
            // function to find the coordinates
            // of the last location
            getWeatherInfoUi()
        }

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        weatherViewModel.weatherInfo.observe(this) { data ->
            Log.d(TAG, "onCreate: weatherViewModel.weatherInfo.observe: data=[$data]")

            // Update UI with the new data
            weatherInfoTextView.text = data
        }
    }

    /**
     * This starts the process of getting weather info.
     * [1] Check permissions or request for permissions
     * [2] Obtain location
     */
    private fun getWeatherInfoUi() {
        Log.d(TAG, "getWeatherInfoUi")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permissions are already granted, obtain the location
            obtainLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, obtain the location
                obtainLocation()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        Log.d(TAG, "obtainLocation")

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
                weatherViewModel.getWeatherInfoApi(this, weatherUrl)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Location Permission not granted", Toast.LENGTH_SHORT).show()

                Log.e(TAG, "obtainLocation: addOnSuccessListener: stackTrace=\n${exception.printStackTrace()}")
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}