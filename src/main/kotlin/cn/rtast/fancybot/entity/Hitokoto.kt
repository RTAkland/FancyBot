/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/21
 */


package cn.rtast.fancybot.entity

import com.google.gson.annotations.SerializedName

data class Hitokoto(
    val uuid: String,
    @SerializedName("hitokoto")
    val sentence: String,
    val from: String
)