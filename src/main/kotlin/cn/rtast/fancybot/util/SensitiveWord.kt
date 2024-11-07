/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/7
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.util.str.proxy

object SensitiveWord {
    private val SENSITIVE_WORD_URL =
        "https://raw.githubusercontent.com/cjh0613/tencent-sensitive-words/refs/heads/main/sensitive_words.txt".proxy
    private const val SEPARATOR = "、"
    private val sensitiveWords = Http.get(SENSITIVE_WORD_URL).split(SEPARATOR)

    fun containsSensitiveWords(input: String): Boolean = sensitiveWords.contains(input)
}