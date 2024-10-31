/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/12
 */


package cn.rtast.fancybot.entity.bili

import com.google.gson.annotations.SerializedName

data class UserInfo(
    val data: Data,
) {
    data class Data(
        val card: Card,
    )

    data class Card(
        val face: String,
        val name: String,
        val fans: Int,
        val sign: String,
        @SerializedName("level_info")
        val levelInfo: LevelInfo,
    )

    data class LevelInfo(
        @SerializedName("current_level")
        val currentLevel: Int,
    )
}