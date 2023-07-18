package com.elvina.easyweather

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.elvina.easyweather.data.ParseJson
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    val LOCATION: String = "41.684994, 44.856963"
    val API: String = "9824a8fcde3d4e4bb7e71518231105"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!API.isEmpty()) {
            fetchWeatherTask().execute()
        } else {
            setViewError("No API key.")
        }

    }

    inner class fetchWeatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            setViewLoading()
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.weatherapi.com/v1/forecast.json?key=$API&q=$LOCATION")
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
}