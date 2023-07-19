package com.elvina.easyweather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.elvina.easyweather.data.ParseJson
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.File
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val API_KEY = getString(R.string.api_key)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gps) {
            setViewError("GPS is disabled.")
        } else if (!API_KEY.isEmpty()) {
            if (checkLocationPermission()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        val LOCATION = location?.latitude.toString() + "," + location?.longitude.toString()
                        fetchWeatherTask(API_KEY, LOCATION).execute()
                    }
                    .addOnFailureListener { e ->
                        setViewError("Couldn't access location")
                    }
            }
        } else {
            setViewError("No API key.")
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
                setViewError("Check internet connection.")
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
                setViewError("Something went wrong, check internet connection")
                println("ERROR: ${e.message}")
            }
        }
    }

    fun setViewSuccess(data: ParseJson) {

        val location = data.locationName()
        val weatherType = data.currentWeatherType()
        val weatherTemp = data.currentWeatherTemp() + " °"
        val weatherIcon = resources.getDrawable(data.currentWeatherIcon())
        val lastUpdatedTime = data.lastUpdatedTime() + "m ago"
        val dayForecast = data.forecastToday()
        val dayMinTemp = "MIN " + dayForecast.getString("mintemp_c")
        val dayMaxTemp = "MAX " + dayForecast.getString("maxtemp_c")

        /* TODO: hourly forecast */
//        val hourForecast = data.forecastHourly().getJSONObject(15)
//        println("CONSOLE: " + hourForecast.getString("temp_c") + " " + hourForecast.getString("time"))

        findViewById<TextView>(R.id.location).text = location
        findViewById<TextView>(R.id.weather_temperature).text = weatherTemp
        findViewById<TextView>(R.id.weather_type).text = weatherType
        findViewById<ImageView>(R.id.weather_icon).background = weatherIcon
        findViewById<TextView>(R.id.update_time).text = lastUpdatedTime
        findViewById<TextView>(R.id.weather_temp_min).text = dayMinTemp
        findViewById<TextView>(R.id.weather_temp_max).text = dayMaxTemp

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

}