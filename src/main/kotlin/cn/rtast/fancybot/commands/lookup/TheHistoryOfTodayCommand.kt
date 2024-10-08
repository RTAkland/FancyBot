/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.tianxing.HistoryOfToday
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import cn.rtast.rob.util.ob.asNode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TheHistoryOfTodayCommand : BaseCommand() {
    override val commandNames = listOf("历史上的今天")

    private val tianXingApiUrl = "https://apis.tianapi.com/lishi/index"
    private val tianXingApiKey = configManager.tianXingApiKey

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"))
        val formatter = DateTimeFormatter.ofPattern("MMdd")
        val currentDate = currentDateTime.format(formatter)
        val messages = mutableListOf<MessageChain>()
        Http.get<HistoryOfToday>(
            tianXingApiUrl,
            mapOf(
                "key" to tianXingApiKey,
                "date" to currentDate,
            )
        ).result.list.forEach {
            messages.add(
                MessageChain.Builder()
                    .addText("事件: ${it.title}")
                    .addNewLine()
                    .addText("发生日期: ${it.lsDate}")
                    .build()
            )
        }
        message.reply(messages.asNode(configManager.selfId))
    }
}