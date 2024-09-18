/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.entity.enums.ImageType
import cn.rtast.fancybot.entity.enums.WSType
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

const val ROOT_PATH = "./data"

val DEFAULT_CONFIG = Config(
    ncmAPI = "https://ncm.rtast.cn",
    wsAddress = "ws://127.0.0.1",
    wsType = WSType.Client,
    accessToken = "114514",
    port = 6760,
    listeningGroups = listOf(114514, 1919810),
    qweatherKey = "114514",
    githubKey = "1919810",
    imageType = ImageType.PNG,
    openAIAPIHost = "https://api.moonshot.cn",
    openAIAPIKey = "114514",
    smtpHost = "127.0.0.1",
    smtpPort = 25,
    smtpUser = "114514",
    smtpPassword = "1919810",
    smtpFromAddress = "fancybot@repo.rtast.cn",
    admins = listOf(3458671395L),
    enableAntiRevoke = true
)