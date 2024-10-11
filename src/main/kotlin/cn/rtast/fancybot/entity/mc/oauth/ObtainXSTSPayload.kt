/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import com.google.gson.annotations.SerializedName

data class ObtainXSTSPayload(
    @SerializedName("Properties")
    val properties: Properties,
    @SerializedName("RelyingParty")
    val relyingParty: String = "rp://api.minecraftservices.com/",
    @SerializedName("TokenType")
    val tokenType: String = "JWT",
) {
    data class Properties(
        @SerializedName("UserTokens")
        val userTokens: List<String>,
        @SerializedName("SandboxId")
        val sandboxId: String = "RETAIL",
    )
}