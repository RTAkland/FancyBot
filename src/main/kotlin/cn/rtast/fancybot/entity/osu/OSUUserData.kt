/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/5
 */


package cn.rtast.fancybot.entity.osu

import com.google.gson.annotations.SerializedName

data class OSUUserData(
    val statistics: Statistics,
    @SerializedName("country_code")
    val countryCode: String,
    val username: String,
) {
    data class Statistics(
        @SerializedName("global_rank")
        val globalRank: Long,
        @SerializedName("play_time")
        val playTime: Long,
        @SerializedName("hit_accuracy")
        val hitAccuracy: Double,
        @SerializedName("country_rank")
        val countryRank: Int,
        val pp: Float,
        val level: Level,
        @SerializedName("play_count")
        val playCount: Int,
    )

    data class Level(
        val current: Int,
        val progress: Int
    )
}