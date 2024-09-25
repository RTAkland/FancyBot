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
    val githubKey get() = this.read<Config>().githubKey
    val imageType get() = this.read<Config>().imageType
    val openAIAPIHost get() = this.read<Config>().openAIAPIHost
    val openAIAPIKey get() = this.read<Config>().openAIAPIKey
    val openAIModel get() = this.read<Config>().openAIModel
    val smtpHost get() = this.read<Config>().smtpHost
    val smtpPort get() = this.read<Config>().smtpPort
    val smtpUser get() = this.read<Config>().smtpUser
    val smtpPassword get() = this.read<Config>().smtpPassword
    val smtpFromAddress get() = this.read<Config>().smtpFromAddress
    val admins get() = this.read<Config>().admins
    val enableAntiRevoke get() = this.read<Config>().enableAntiRevoke
    val llamaUrl get() = this.read<Config>().llamaUrl
    val llamaModel get() = this.read<Config>().llamaModel
    val qqMusicApiUrl get() = this.read<Config>().qqMusicApiUrl
}