/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.entity.bili

data class VideoStat(
    val data: Data,
) {
    data class Data(
        val aid: Long,
        val pic: String,
        val title: String,
        val owner: Owner,
        val stat: Stat,
        val cid: Long,
    )

    data class Owner(
        val mid: Long,
        val name: String,
        val face: String,
    )

    data class Stat(
        val view: Int,
        val favorite: Int,
        val coin: Int,
        val like: Int,
        val reply: Int,
        val share: Int,
    )
}