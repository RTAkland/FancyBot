/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.entity.github

import com.google.gson.annotations.SerializedName

data class UserInfo(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val company: String?,
    val email: String?,
    val bio: String?,
    val followers: Int,
    val following: Int,
    val location: String?,
)