/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.entity.bili

data class BVID(
    val data: Data
) {
    data class Data(
        val pic: String,
        val title: String,
        val owner: Owner,
        val stat: Stat,
        val cid: Long,
    )

    data class Owner(
        val name: String
    )

    data class Stat(
        val view: Long,
        val favorite: Int,
        val coin: Int,
        val like: Int,
    )
}