package com.elvina.easyweather

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    val LOCATION: String = "Tbilisi"
    val API: String = "9824a8fcde3d4e4bb7e71518231105"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!API.isEmpty()) {
            fetchWeatherTask().execute()
        } else {
            findViewById<RelativeLayout>(R.id.main_container).visibility = View.GONE
            findViewById<TextView>(R.id.error_message).visibility = View.VISIBLE
            findViewById<TextView>(R.id.error_message).text = "No API key."
            println("CONSOLE: NO API KEY")
        }

    }

    inner class fetchWeatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.main_container).visibility = View.GONE
            findViewById<TextView>(R.id.error_message).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response = URL("https://api.weatherapi.com/v1/forecast.json?key=$API&q=$LOCATION")
                    .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                println("CONSOLE: doInBackground Catch" + e.message)
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val json = JSONObject(result)
                val data = ParseJson(JSONObject(result))

                val location = data.locationName()
                val weatherType = data.currentWeatherType()
                val weatherTemp = data.currentWeatherTemp()
                val lastUpdatedTime = data.lastUpdatedTime()
                val dayForecast = data.forecastToday()

                /* TODO: hourly forecast */
//                val hourForecast = data.forecastHourly().getJSONObject(15)
//                println("CONSOLE: " + hourForecast.getString("temp_c") + " " + hourForecast.getString("time"))

                /* TODO: weather icon */
                findViewById<ImageView>(R.id.weather_icon).background = resources.getDrawable(data.currentWeatherIcon())

                findViewById<TextView>(R.id.location).text = location
                findViewById<TextView>(R.id.weather_temperature).text = weatherTemp + " °"
                findViewById<TextView>(R.id.weather_type).text = weatherType
                findViewById<TextView>(R.id.update_time).text = lastUpdatedTime + "m ago"
                findViewById<TextView>(R.id.weather_temp_min).text = "MIN " + dayForecast.getString("mintemp_c")
                findViewById<TextView>(R.id.weather_temp_max).text = "MAX " + dayForecast.getString("maxtemp_c")

                findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.main_container).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
                findViewById<TextView>(R.id.error_message).visibility = View.VISIBLE
                findViewById<TextView>(R.id.error_message).text = e.message
            }
        }
    }
}