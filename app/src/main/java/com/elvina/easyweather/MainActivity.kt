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

class MainActivity : AppCompatActivity() {

    val LOCATION: String = "Tbilisi"
    val API: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        println("CONSOLE: START")
        fetchWeatherTask().execute()
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
            println("CONSOLE: " + 2.1)
            var response: String?
            try{
                println("CONSOLE: "+ 2.2)
                response = URL(
                    "https://api.weatherapi.com/v1/forecast.json?key=$API&q=$LOCATION")
                    .readText(Charsets.UTF_8)
//                response = URL("https://api.weatherapi.com/v1/current.json?key=9824a8fcde3d4e4bb7e71518231105&q=London").readText(Charsets.UTF_8)
                println("CONSOLE: " + 2.3)
            }
            catch(e: Exception){
                println("CONSOLE: "+e.message)
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val json = JSONObject(result)
                val data = json.getJSONObject("location")
                val name = data.getString("name")

                println("CONSOLE: "+name)
                println("CONSOLE: END")

				findViewById<TextView>(R.id.weather_temperature).text = name
				findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
				findViewById<RelativeLayout>(R.id.main_container).visibility = View.VISIBLE
            }
            catch (e: Exception){
				findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
				findViewById<TextView>(R.id.error_message).visibility = View.VISIBLE
                findViewById<TextView>(R.id.error_message).text = e.message
            }
        }
    }
}