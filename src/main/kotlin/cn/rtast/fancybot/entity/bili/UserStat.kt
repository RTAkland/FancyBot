/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/13
 */


package cn.rtast.fancybot.entity.bili

data class UserStat(
    val data: Data,
) {
    data class Data(
        val follower: Int,
    )
}