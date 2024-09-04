/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.util.JsonFileHandler

class ConfigManager : JsonFileHandler<Config>("config.json") {
    val ncmAPI get() = this.read<Config>().ncmAPI
    val wsAddress get() = this.read<Config>().wsAddress
    val wsPort get() = this.read<Config>().port
    val accessToken get() = this.read<Config>().accessToken
    val wsType get() = this.read<Config>().wsType
    val listeningGroups get() = this.read<Config>().listeningGroups
    val qweatherKey get() = this.read<Config>().qweatherKey
}