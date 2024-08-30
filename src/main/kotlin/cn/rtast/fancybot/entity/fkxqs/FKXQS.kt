/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.entity.fkxqs

data class FKXQS(
    val data: Data,
) {
    data class Data(
        val text: String,
    )
}