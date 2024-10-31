/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.entity.bgm

import com.google.gson.annotations.SerializedName

data class BGMSearch(
    val list: List<Result>,
) {
    data class Result(
        val url: String,
        val images: Images,
        val name: String,
        @SerializedName("name_cn")
        val nameCN: String,
    )

    data class Images(
        val large: String,
    )
}