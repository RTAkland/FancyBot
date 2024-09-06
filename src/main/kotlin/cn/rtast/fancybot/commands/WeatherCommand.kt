/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.weather.Geo
import cn.rtast.fancybot.entity.weather.Weather
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class WeatherCommand : BaseCommand() {
    override val commandNames = listOf("/weather", "/天气")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送/weather <地区名>即可查看实时天气哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val locationName = args.first()
        val lookupLocationId = Http.get<Geo>(
            "https://geoapi.qweather.com/v2/city/lookup",
            mapOf("location" to locationName, "key" to configManager.qweatherKey)
        ).location.first().id
        val weather = Http.get<Weather>(
            "https://devapi.qweather.com/v7/weather/now",
            mapOf("location" to lookupLocationId, "key" to configManager.qweatherKey)
        )
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addNewLine()
            .addText("城市: $locationName, 温度: ${weather.now.temp} ℃")
            .addNewLine()
            .addText("${weather.now.text}/${weather.now.windDir}")
            .addNewLine()
            .addText("数据来源: ${weather.refer.sources.joinToString(",")}")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}