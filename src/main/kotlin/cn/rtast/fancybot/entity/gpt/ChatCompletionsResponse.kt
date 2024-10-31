/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.entity.gpt

import cn.rtast.fancybot.enums.GPTUserRole

data class ChatCompletionsResponse(
    val choices: List<Choice>,
) {
    data class Choice(
        val message: Message,
    )

    data class Message(
        val role: GPTUserRole,
        val content: String,
    )
}