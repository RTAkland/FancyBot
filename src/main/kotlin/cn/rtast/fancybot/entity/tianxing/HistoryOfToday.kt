/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.entity.tianxing

import com.google.gson.annotations.SerializedName

data class HistoryOfToday(
    val result: Result
) {
    data class Result(
        val list: List<Element>
    )

    data class Element(
        val title: String,
        @SerializedName("lsdate")
        val lsDate: String
    )
}