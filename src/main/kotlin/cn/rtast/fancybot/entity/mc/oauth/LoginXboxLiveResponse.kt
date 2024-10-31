/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import com.google.gson.annotations.SerializedName

data class LoginXboxLiveResponse(
    @SerializedName("Token")
    val token: String,
    @SerializedName("DisplayClaims")
    val displayClaims: DisplayClaims,
) {
    data class DisplayClaims(
        val xui: List<XUI>,
    )

    data class XUI(
        val uhs: String,
    )
}