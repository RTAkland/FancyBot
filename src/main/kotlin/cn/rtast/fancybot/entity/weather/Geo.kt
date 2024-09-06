/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.entity.weather

data class Geo(
    val location: List<Location>
) {
    data class Location(
        val name: String,
        val id: String,
        val adm1: String,
        val adm2: String,
        val country: String,
    )
}