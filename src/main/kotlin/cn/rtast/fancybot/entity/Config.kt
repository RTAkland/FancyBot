/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.entity

import cn.rtast.fancybot.entity.enums.ImageType
import cn.rtast.fancybot.entity.enums.WSType

data class Config(
    val ncmAPI: String,
    val wsType: WSType,
    val wsAddress: String,
    val accessToken: String,
    val port: Int,
    val listeningGroups: List<Long>,
    val qweatherKey: String,
    val githubKey: String,
    val imageType: ImageType,
)