/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import com.google.gson.annotations.SerializedName

data class RedeemCodeResponse(
    @SerializedName("access_token")
    val accessToken: String,
)