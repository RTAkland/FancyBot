/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.weather.Geo
import cn.rtast.fancybot.entity.weather.Weather
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.drawCenteredText
import cn.rtast.fancybot.util.drawCustomImage
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.toBufferedImage
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage

class WeatherCommand : BaseCommand() {
    override val commandNames = listOf("/weather", "/天气")

    private val canvasWidth = 800
    private val canvasHeight = 600
    private val font = Font("Serif", Font.ITALIC, 60).deriveFont(Font.BOLD)
    private val locationFont = Font("Serif", Font.ITALIC, 50)

    private fun createWeatherCard(weather: Weather, geoResult: Geo.Location): String {
        val emptyImage = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = emptyImage.createGraphics()
        val weatherIcon = Resources.loadFromResourcesAsBytes("qweather/${weather.now.icon}.png")
            ?: Resources.loadFromResourcesAsBytes("qweather/100.png")!!
        g2d.color = Color(209, 238, 238)
        g2d.fillRect(0, 0, canvasWidth, canvasHeight)
        g2d.drawCustomImage(weatherIcon.toBufferedImage(), canvasWidth / 2 - 230, canvasHeight / 2 - 80, 230.0, 230.0)
        g2d.color = Color.BLACK
        g2d.font = font
        g2d.drawString("${weather.now.temp}℃", canvasWidth / 2, canvasHeight / 2 + 20)
        g2d.drawString(weather.now.text, canvasWidth / 2, canvasHeight / 2 + 100)
        g2d.font = locationFont
        g2d.drawCenteredText("${geoResult.adm1}-${geoResult.adm2}-${geoResult.name}", canvasWidth / 2, 90)
        g2d.color = Color(155, 48, 255)
        g2d.drawCenteredText("数据来源: ${weather.refer.sources.joinToString(",")}", canvasWidth / 2, canvasHeight - 80)
        return emptyImage.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送/weather <地区名>即可查看实时天气哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        try {
            val locationName = args.first()
            val lookupLocation = Http.get<Geo>(
                "https://geoapi.qweather.com/v2/city/lookup",
                mapOf("location" to locationName, "key" to configManager.qweatherKey)
            ).location.first()
            val weather = Http.get<Weather>(
                "https://devapi.qweather.com/v7/weather/now",
                mapOf("location" to lookupLocation.id, "key" to configManager.qweatherKey)
            )
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addImage(this.createWeatherCard(weather, lookupLocation), true)
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        } catch (_: NullPointerException) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("没有查询到该城市的天气信息哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}