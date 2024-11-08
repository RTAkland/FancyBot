/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/28
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.ROOT_PATH
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.setu.Setu
import cn.rtast.fancybot.entity.setu.SetuV3
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.onebot.NodeMessageChain
import cn.rtast.rob.onebot.toNode
import java.io.File
import java.net.URI

private val setuV3Index = File("$ROOT_PATH/caches/pixiv_index_v3.json")
    .readText(Charsets.UTF_8)
    .fromArrayJson<List<SetuV3>>()

private fun getImages(r18: Boolean): NodeMessageChain {
    val msg = mutableListOf<MessageChain>()
    if (r18) {
        val response = setuV3Index.shuffled().take(10)
        msg.add(MessageChain.Builder().addText("因为是R18所以你需要自行访问链接来查看图片~").build())
        response.forEach {
            try {
                val tempMsg = MessageChain.Builder()
                    .addText(it.url)
                    .addNewLine()
                    .addText("标题: ${it.title}")
                    .addNewLine()
                    .addText("作品ID: ${it.pid}")
                    .addNewLine()
                    .addText("作者: ${it.author}(${it.uid})")
                    .build()
                msg.add(tempMsg)
            } catch (_: Exception) {
            }
        }
    } else {
        val response = Http.get("$API_RTAST_URL/api/setu?size=10").fromArrayJson<List<Setu>>()
        response.filter { !it.r18 }.map { it.urls.large }.forEach {
            try {
                val imageBase64 = URI(it).toURL().readBytes().encodeToBase64()
                val tempMsg = MessageChain.Builder().addImage(imageBase64, true).build()
                msg.add(tempMsg)
            } catch (_: Exception) {
            }
        }
    }
    return msg.toNode(configManager.selfId)
}

class TenSetuItem : Item() {
    override val itemNames = listOf("十张色图")
    override val itemPrice = 20.0

    override suspend fun redeemInGroup(message: GroupMessage, after: Double): MessageChain.Builder {
        val msg = getImages(false)
        message.reply(msg)
        return MessageChain.Builder()
    }
}

class TenSetuR18Item : Item() {
    override val itemNames = listOf("十张色图r")
    override val itemPrice = 50.0

    override suspend fun redeemInGroup(message: GroupMessage, after: Double): MessageChain.Builder {
        message.reply(getImages(true))
        return MessageChain.Builder()
    }
}