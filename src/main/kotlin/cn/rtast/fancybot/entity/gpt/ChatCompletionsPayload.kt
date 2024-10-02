/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.entity.gpt

import cn.rtast.fancybot.enums.GPTUserRole

data class ChatCompletionsPayload(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
) {
    data class Message(
        val content: String,
        val role: GPTUserRole = GPTUserRole.user,
        val temperature: Float = 0.3f
    )
}