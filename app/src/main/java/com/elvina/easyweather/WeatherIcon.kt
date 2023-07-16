package com.elvina.easyweather

fun weatherIcon(code: Int): String {
    val code: Int = 1

    return when (code) {
        1000 -> "clear"
        1003, 1006, 1009 -> "cloudy"
        1072, 1150, 1153, 1168, 1171 -> "drizzle"
        1135, 1147 -> "fog"
        1030 -> "mist"
        1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246 -> "rain"
        1069, 1204, 1207, 1249, 1252 -> "sleet"
        1066, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1237, 1255, 1258, 1261, 1264, 1279, 1282 -> "snow"
        1087 -> "thunder"
        else -> "unknown"
    }
}