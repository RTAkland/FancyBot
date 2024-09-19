/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/19
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.entity.wiki.WikipediaResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import org.jsoup.Jsoup

class WikipediaCommand : BaseCommand() {
    override val commandNames = listOf("/wiki", "/wk")

    private val wikipediaAPI = "https://proxy.rtast.cn/https/zh.wikipedia.org/w/api.php"

    private fun String.extractPlainTextFromHtml(): String {
        val doc = Jsoup.parse(this)
        return doc.text()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val title = args.first()
        val response = Http.get<WikipediaResponse>(
            wikipediaAPI,
            mapOf(
                "action" to "query",
                "list" to "search",
                "srsearch" to title,
                "format" to "json"
            )
        ).query.search.first()
        val msg = MessageChain.Builder()
            .addText("标题: ${response.title} 内容片段如下")
            .addNewLine()
            .addText(response.snippet.extractPlainTextFromHtml())
            .build()
        message.reply(msg)
    }
}