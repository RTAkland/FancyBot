/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.entity.github

data class UploadContentResponse(
    val content: Content,
) {
    data class Content(
        val name: String,
    )
}