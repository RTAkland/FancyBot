/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import cn.rtast.fancybot.entity.mc.oauth.LoginXboxLiveResponse.DisplayClaims
import com.google.gson.annotations.SerializedName

data class ObtainXSTSResponse(
    @SerializedName("Token")
    val token: String,
    @SerializedName("DisplayClaims")
    val displayClaims: DisplayClaims,
)
