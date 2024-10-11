/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.entity.mc.oauth

import com.google.gson.annotations.SerializedName

data class DeviceCodeResponse(
    @SerializedName("user_code")
    val userCode: String,
    @SerializedName("device_code")
    val deviceCode: String,
    @SerializedName("verification_uri")
    val verificationUri: String
)