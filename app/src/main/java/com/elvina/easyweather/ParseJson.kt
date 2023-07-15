package com.elvina.easyweather

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class ParseJson(val json: JSONObject) {

    fun locationName(): String {
        return json.getJSONObject("location").getString("name")
    }

    fun currentWeatherType(): String {
        return json.getJSONObject("current").getJSONObject("condition").getString("text")
//        val weatherType = dataCurrent.getJSONObject("condition").getString("text")
    }

    fun currentWeatherTemp(): String {
        return json.getJSONObject("current").getInt("temp_c").toString()
    }

    fun lastUpdatedTime(): String {
        val oldDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm").parse(json.getJSONObject("current").getString("last_updated"))
        val currentDate = Date()
        val diff = currentDate.getTime() - oldDate.getTime()
        val diffM = (diff / 1000) / 60
        return diffM.toString()
    }

    fun forecastToday(): JSONObject {
        val forecast = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0)
        return forecast.getJSONObject("day")
    }

    fun forecastHourly(): JSONArray {
        val forecast =
            json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0)
        return forecast.getJSONArray("hour")
    }
}