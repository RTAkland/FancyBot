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
        val id: String
    )
}