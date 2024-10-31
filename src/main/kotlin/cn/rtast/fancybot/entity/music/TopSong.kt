/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/3
 */


package cn.rtast.fancybot.entity.music

data class TopSong(
    val data: List<Data>,
) {
    data class Data(
        val privilege: Privilege,
    )

    data class Privilege(
        val id: Long,
    )
}