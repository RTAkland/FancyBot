/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.entity.gpt

data class LlamaResponse(
    val message: Message
) {
    data class Message(
        val content: String,
    )
}