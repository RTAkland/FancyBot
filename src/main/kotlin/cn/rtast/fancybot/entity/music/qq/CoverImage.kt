/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/25
 */


package cn.rtast.fancybot.entity.music.qq

data class CoverImage(
    val response: Response
) {
    data class Response(
        val data: Data
    )

    data class Data(
        val imageUrl: String
    )
}