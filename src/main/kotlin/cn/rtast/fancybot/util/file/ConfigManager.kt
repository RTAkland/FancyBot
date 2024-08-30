/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.util.JsonFileHandler

class ConfigManager : JsonFileHandler<Config>("config.json") {

    val ncmAPI get() = this.read<Config>().cnmApi
}