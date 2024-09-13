/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/13
 */


package cn.rtast.fancybot.entity.compiler

data class GLOTPayload(
    val files: List<File>
) {
    data class File(
        val content: String,
        val name: String = "main"
    )
}