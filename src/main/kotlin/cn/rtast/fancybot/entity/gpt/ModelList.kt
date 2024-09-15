/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.entity.gpt

data class ModelList(
    val data: List<Data>
) {
    data class Data(
        val id: String,
    )
}