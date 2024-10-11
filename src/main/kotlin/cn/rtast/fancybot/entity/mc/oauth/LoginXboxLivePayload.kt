/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import com.google.gson.annotations.SerializedName

data class LoginXboxLivePayload(
    @SerializedName("Properties")
    val properties: Properties,
    @SerializedName("RelyingParty")
    val relyingParty: String = "http://auth.xboxlive.com",
    @SerializedName("TokenType")
    val tokenType: String = "JWT",
) {
    data class Properties(
        @SerializedName("RpsTicket")
        val rpsTicket: String,
        @SerializedName("AuthMethod")
        val authMethod: String = "RPS",
        @SerializedName("SiteName")
        val siteName: String = "user.auth.xboxlive.com",
    )
}