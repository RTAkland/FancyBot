/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/9
 */


package cn.rtast.fancybot.entity.mc

import cn.rtast.fancybot.enums.mc.MCVersionType

data class MCVersion(
    val latest: Latest,
    val versions: List<Version>
) {
    data class Latest(
        val release: String,
        val snapshot: String
    )

    data class Version(
        val id: String,
        val type: MCVersionType,
        val releaseTime: String
    )
}