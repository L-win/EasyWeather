package com.elvina.easyweather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.elvina.easyweather.data.ParseJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Read api key from api_key.xml. This file is hidden from public via .gitignore.
        val API_KEY = getString(R.string.api_key)


        // Access location provider services for later use.
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Accessing GPS module of smartphone, if it is disable weather will not be shown.
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


        // Check all the necessary conditions for requesting weather api.
        if (!gps) {
            setViewError("Enable Location and refresh")
        } else if (!API_KEY.isEmpty()) {

            // In first run permission will be requested from user to access location data.
            if (checkLocationPermission()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        val LOCATION = location?.latitude.toString() + "," + location?.longitude.toString()
                        fetchWeatherTask(API_KEY, LOCATION).execute()
                    }
                    .addOnFailureListener { e ->
                        setViewError("Couldn't access location")
                    }
            } else {
                setViewError("Permission is required")
            }
        } else {
            setViewError("No API key")
        }

    }

    inner class fetchWeatherTask(val apiKey: String, val location: String) : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            setViewLoading()
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=$location")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val data = ParseJson(JSONObject(result))
                setViewSuccess(data)
            } catch (e: Exception) {
                setViewError("Check internet connection")
                println("ERROR: ${e.message}")
            }
        }
    }

    fun setViewSuccess(data: ParseJson) {

        // General weather data
        val location = data.locationName()
        val weatherType = data.currentWeatherType()
        val weatherTemp = data.currentWeatherTemp() + " °"
        val weatherIcon = resources.getDrawable(data.currentWeatherIcon())
        val lastUpdatedTime = data.lastUpdatedTime() + "m ago"
        val dayForecast = data.forecastToday()
        val dayMinTemp = dayForecast.getString("mintemp_c") + " °"
        val dayMaxTemp = dayForecast.getString("maxtemp_c") + " °"

        findViewById<TextView>(R.id.location).text = location
        findViewById<TextView>(R.id.weather_temperature).text = weatherTemp
        findViewById<TextView>(R.id.weather_type).text = weatherType
        findViewById<ImageView>(R.id.weather_icon).background = weatherIcon
        findViewById<TextView>(R.id.update_time).text = lastUpdatedTime
        findViewById<TextView>(R.id.weather_temp_min).text = dayMinTemp
        findViewById<TextView>(R.id.weather_temp_max).text = dayMaxTemp


        // Hourly forecast
        val hour8 = data.forecastHourly(8) + "°"
        val hour12 = data.forecastHourly(12) + "°"
        val hour16 = data.forecastHourly(16) + "°"
        val hour19 = data.forecastHourly(19) + "°"
        val hour22 = data.forecastHourly(22) + "°"

        findViewById<TextView>(R.id.hour8).text = hour8
        findViewById<TextView>(R.id.hour12).text = hour12
        findViewById<TextView>(R.id.hour16).text = hour16
        findViewById<TextView>(R.id.hour19).text = hour19
        findViewById<TextView>(R.id.hour22).text = hour22

        findViewById<ImageView>(R.id.hour8_image).background = resources.getDrawable(data.hourlyWeatherIcon(8))
        findViewById<ImageView>(R.id.hour12_image).background = resources.getDrawable(data.hourlyWeatherIcon(12))
        findViewById<ImageView>(R.id.hour16_image).background = resources.getDrawable(data.hourlyWeatherIcon(16))
        findViewById<ImageView>(R.id.hour19_image).background = resources.getDrawable(data.hourlyWeatherIcon(19))
        findViewById<ImageView>(R.id.hour22_image).background = resources.getDrawable(data.hourlyWeatherIcon(22))

        // Conditional UI elements
        findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.main_container).visibility = View.VISIBLE
    }

    fun setViewLoading() {
        findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.main_container).visibility = View.GONE
        findViewById<TextView>(R.id.error_message).visibility = View.GONE
    }

    fun setViewError(message: String?) {
        findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.main_container).visibility = View.GONE
        findViewById<TextView>(R.id.error_message).visibility = View.VISIBLE
        findViewById<TextView>(R.id.error_message).text = message
        findViewById<Button>(R.id.refresh).visibility = View.VISIBLE

        findViewById<Button>(R.id.refresh).setOnClickListener {
            restartActivity()
        }
    }

    fun checkLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults.sum() == 0) restartActivity()
    }

    fun restartActivity() {
        val mIntent = intent
        finish()
        startActivity(mIntent)
    }
}