package com.elvina.easyweather

fun weatherIconNight(code: Int): Int {
    return when (code) {
        1000 -> R.drawable.clear_night_time
        1030 -> R.drawable.fog_night_time
        1087 -> R.drawable.lightning_night_time
        1135, 1147 -> R.drawable.fog_night_time
        1003, 1006, 1009 -> R.drawable.cloudy_night_time
        1072, 1150, 1153, 1168, 1171 -> R.drawable.drizzle_night_time
        1069, 1204, 1207, 1249, 1252 -> R.drawable.sleet_night_time
        1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246 -> R.drawable.rain_night_time

        1066, 1114, 1117, 1210, 1213,
        1216, 1219, 1222, 1225, 1237,
        1255, 1258, 1261, 1264, 1279, 1282 -> R.drawable.snow_night_time

        else -> R.drawable.placeholder_alien
    }
}

fun weatherIconDay(code: Int): Int {
    return when (code) {
        1000 -> R.drawable.clear_day_time
        1030 -> R.drawable.fog_day_time
        1087 -> R.drawable.lightning_day_time
        1135, 1147 -> R.drawable.fog_day_time
        1003, 1006, 1009 -> R.drawable.cloudy_day_time
        1072, 1150, 1153, 1168, 1171 -> R.drawable.drizzle_day_time
        1069, 1204, 1207, 1249, 1252 -> R.drawable.sleet_day_time
        1063, 1180, 1183, 1186, 1189, 1192, 1195, 1198, 1201, 1240, 1243, 1246 -> R.drawable.rain_day_time

        1066, 1114, 1117, 1210, 1213,
        1216, 1219, 1222, 1225, 1237,
        1255, 1258, 1261, 1264, 1279, 1282 -> R.drawable.snow_day_time

        else -> R.drawable.placeholder_alien
    }
}