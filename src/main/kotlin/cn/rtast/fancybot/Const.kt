/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.entity.enums.WSType
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .serializeNulls()
    .create()

const val ROOT_PATH = "./data"

val DEFAULT_CONFIG = Config(
    ncmAPI = "https://ncm.rtast.cn",
    wsAddress = "ws://127.0.0.1",
    wsType = WSType.Client,
    accessToken = "114514",
    port = 6760
)
