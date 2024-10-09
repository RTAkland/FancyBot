/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/9
 */


package cn.rtast.fancybot.util.file

fun ByteArray.getFileType(): String {
    val fileSignature = this.take(4).joinToString("") { "%02X".format(it) }
    return when {
        fileSignature.startsWith("FFD8FF") -> "jpg"
        fileSignature.startsWith("89504E47") -> "png"
        fileSignature.startsWith("47494638") -> "gif"
        fileSignature.startsWith("25504446") -> "pdf"
        fileSignature.startsWith("504B0304") -> "zip"
        fileSignature.startsWith("377ABCAF") -> "7z"
        fileSignature.startsWith("1F8B") -> "gz"
        else -> ""
    }
}