package com.example.weatherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

private const val TAG = "TOWM: MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var getWeatherButton : Button
    private lateinit var weatherInfoTextView: TextView

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // link the textView in which the
        // temperature will be displayed
        weatherInfoTextView = findViewById(R.id.weather_info_text_view)

        getWeatherButton = findViewById(R.id.get_weather_button)

        // on clicking this button function to
        // get the coordinates will be called
        getWeatherButton.setOnClickListener {
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
            weatherViewModel.obtainLocation(this, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, obtain the location
                weatherViewModel.obtainLocation(this, this)
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}