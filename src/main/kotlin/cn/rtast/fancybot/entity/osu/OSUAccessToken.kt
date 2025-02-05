/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/5
 */


package cn.rtast.fancybot.entity.osu

import com.google.gson.annotations.SerializedName

data class OSUAccessToken(
    @SerializedName("access_token")
    val accessToken: String,
)