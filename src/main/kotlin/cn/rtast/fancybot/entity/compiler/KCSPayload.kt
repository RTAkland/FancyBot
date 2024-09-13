/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.entity.compiler

data class KCSPayload(
    val files: List<File>,
    val args: String = "",
    val confType: String = "java"
) {
    data class File(
        val text: String,
        val name: String = "File.kt",
        val publicId: String = ""
    )
}