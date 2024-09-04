/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.entity

data class MCPing(
    val version: Version,
    val players: Players,
    val favicon: String?
) {
    data class Version(
        val protocol: Int,
        val name: String,
    )

    data class Players(
        val online: Int,
        val max: Int,
    )
}