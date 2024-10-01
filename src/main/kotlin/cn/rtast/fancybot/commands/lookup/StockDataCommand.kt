/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/1
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.stock.StockSymbolQuery
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import cn.rtast.rob.util.ob.asNode

class StockDataCommand : BaseCommand() {
    override val commandNames = listOf("sd", "股票")

    private val apiUrl = "https://proxy.rtast.cn/https/finnhub.io"
    private val apiKey = "crtmph1r01qv68qhn8n0crtmph1r01qv68qhn8ng"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val method = args.first()
        when (method) {
            "q" -> {
                val messages = mutableListOf<MessageChain>()
                val symbol = args.joinToString(" ")
                val response = Http.get<StockSymbolQuery>(
                    "$apiUrl/api/v1/search",
                    mapOf(
                        "q" to symbol,
                        "token" to apiKey,
                    )
                )
                messages.add(MessageChain.Builder().addText("共搜索到${response.count}条结果").build())
                response.result.forEach {
                    val tempMsg = MessageChain.Builder()
                        .addText("股票代码: ${it.displaySymbol}(${it.symbol}, ${it.description})")
                        .build()
                    messages.add(tempMsg)
                }
                message.reply(messages.asNode(configManager.selfId))
            }

        }
    }
}