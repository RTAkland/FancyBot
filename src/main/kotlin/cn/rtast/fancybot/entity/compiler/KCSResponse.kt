/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.entity.compiler

import com.google.gson.annotations.SerializedName

data class KCSResponse(
    val errors: Errors,
    val exception: Any,
    val jvmByteCode: Any,
    val text: String,
) {
    data class Errors(
        @SerializedName("File.kt")
        val file: List<Any>,
    )
}