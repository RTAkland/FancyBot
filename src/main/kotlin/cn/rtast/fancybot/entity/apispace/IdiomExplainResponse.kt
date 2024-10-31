/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.entity.apispace

data class IdiomExplainResponse(
    val data: List<Data>,
) {
    data class Data(
        val name: String,
        val sound: String,
        val explanation: String,
        val provenance: String,
    )
}