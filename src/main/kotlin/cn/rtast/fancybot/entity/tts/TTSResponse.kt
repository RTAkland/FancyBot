/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/7
 */


package cn.rtast.fancybot.entity.tts

import com.google.gson.annotations.SerializedName

data class TTSResponse(
    @SerializedName("URL")
    val url: String
)