package com.elvina.easyweather

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    val LOCATION: String = "Tbilisi"
    val API: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("CONSOLE: START")
        if(!API.isEmpty()){
            fetchWeatherTask().execute()
        }else{
            println("CONSOLE: NO API KEY")
        }

    }
    inner class fetchWeatherTask() : AsyncTask<String, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.main_container).visibility = View.GONE
            findViewById<TextView>(R.id.error_message).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            println("CONSOLE: doInBackground")
            var response: String?
            try{
                println("CONSOLE: doInBackground Try")
                response = URL(
                    "https://api.weatherapi.com/v1/forecast.json?key=$API&q=$LOCATION")
                    .readText(Charsets.UTF_8)
                println("CONSOLE: doInBackground Try 2")
            }
            catch(e: Exception){
                println("CONSOLE: doInBackground Catch" + e.message)
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val json = JSONObject(result)

                val location = json.getJSONObject("location").getString("name")
                val dataCurrent = json.getJSONObject("current")
                val weatherType = dataCurrent.getJSONObject("condition").getString("text")
                val weatherTemp = dataCurrent.getInt("temp_c")
                val updateDateTime = dataCurrent.getString("last_updated")
                val time = SimpleDateFormat("hh:mm", Locale.ENGLISH)
                val formattedDateTime = time.format(Date(dataCurrent.getLong("last_updated_epoch")*1000))

                findViewById<TextView>(R.id.location).text = location
				findViewById<TextView>(R.id.weather_temperature).text = weatherTemp.toString() + " °"
                findViewById<TextView>(R.id.weather_type).text = weatherType
                findViewById<TextView>(R.id.update_time).text = formattedDateTime

				findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
				findViewById<RelativeLayout>(R.id.main_container).visibility = View.VISIBLE

                println("CONSOLE: END")
            }
            catch (e: Exception){
				findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
				findViewById<TextView>(R.id.error_message).visibility = View.VISIBLE
                findViewById<TextView>(R.id.error_message).text = e.message
            }
        }
    }
}