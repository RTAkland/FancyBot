/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.entity.weather

data class Weather(
    val now: Now,
    val refer: Refer
) {
    data class Now(
        val temp: String,
        val text: String,
        val windDir: String,
        val icon: String,
    )

    data class Refer(
        val sources: List<String>
    )
}