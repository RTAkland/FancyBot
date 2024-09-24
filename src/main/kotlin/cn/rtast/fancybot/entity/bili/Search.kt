/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.entity.bili

import com.google.gson.annotations.SerializedName

data class Search(
    val data: Data
) {
    data class Data(
        val result: List<Result>
    )

    data class Result(
        @SerializedName("result_type")
        val resultType: String,
        val data: List<EPData>
    )

    data class EPData(
        val url: String,
        val desc: String,
        val title: String,
        @SerializedName("org_title")
        val orgTitle: String,
        val cover: String,
        @SerializedName("pubtime")
        val pubTime: Long,
        @SerializedName("media_score")
        val mediaScore: MediaScore,
        @SerializedName("index_show")
        val indexShow: String,
        val areas: String,
        val styles: String,
    )

    data class MediaScore(
        val score: Float,
        @SerializedName("user_count")
        val userCount: Int,
    )
}