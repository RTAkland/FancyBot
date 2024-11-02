/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.util.str.JsonFileHandler

class ConfigManager : JsonFileHandler<Config>("config.json") {
    private val config = this.read<Config>()
    val ncmAPI = config.ncmAPI
    val wsAddress = config.wsAddress
    val wsPort = config.port
    val accessToken = config.accessToken
    val wsType = config.wsType
    val listeningGroups = config.listeningGroups
    val qweatherKey = config.qweatherKey
    val githubKey = config.githubKey
    val imageType = config.imageType
    val openAIAPIHost = config.openAIAPIHost
    val openAIAPIKey = config.openAIAPIKey
    val openAIModel = config.openAIModel
    val smtpHost = config.smtpHost
    val smtpPort = config.smtpPort
    val smtpUser = config.smtpUser
    val smtpPassword = config.smtpPassword
    val smtpFromAddress = config.smtpFromAddress
    val admins = config.admins
    val enableAntiRevoke = config.enableAntiRevoke
    val llamaUrl = config.llamaUrl
    val llamaModel = config.llamaModel
    val qqMusicApiUrl = config.qqMusicApiUrl
    val noticeUser = config.noticeUser
    val selfId = config.selfId
    val tianXingApiKey = config.tianXingApiKey
    val githubUser = config.githubUser
    val githubImageRepo = config.githubImageRepo
    val imageBedType = config.imageBedType
    val cloudflareAccountId = config.cloudflareAccountId
    val cloudflareR2AccessKey = config.cloudflareR2AccessKeyId
    val cloudflareR2SecretKey = config.cloudflareR2SecretKey
    val cloudflareR2BucketName = config.cloudflareR2BucketName
    val cloudflareR2PublicUrl = config.cloudflareR2PublicUrl
    val apiSpaceKey = config.apiSpaceKey
    val apiRtastKey = config.apiRtastKey
    val azureAppClientId = config.azureAppClientId
    val youtubeDataApiKey = config.youtubeDataApiKey
    val naiLongApiUrl = config.naiLongApiUrl
}