/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/19
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.wiki.PageInfoResponse
import cn.rtast.fancybot.entity.wiki.WikipediaResponse
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import org.jsoup.Jsoup

@CommandDescription("查询wiki百科")
class WikipediaCommand : BaseCommand() {
    override val commandNames = listOf("/wiki", "/wk")

    private val wikipediaAPI = "https://proxy.rtast.cn/https/zh.wikipedia.org/w/api.php"

    private fun String.extractPlainTextFromHtml(): String {
        val doc = Jsoup.parse(this)
        return doc.text()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("发送`/wiki <关键字>`即可查询出最匹配的wiki页面~")
                .build()
            message.reply(msg)
        }
        val title = args.first()
        try {
            val response = Http.get<WikipediaResponse>(
                wikipediaAPI,
                mapOf(
                    "action" to "query",
                    "list" to "search",
                    "srsearch" to title,
                    "format" to "json"
                )
            ).query.search.first()
            val fullUrl = Http.get<PageInfoResponse>(
                wikipediaAPI,
                mapOf(
                    "action" to "query",
                    "prop" to "info",
                    "pageids" to response.pageId,
                    "inprop" to "url",
                    "format" to "json"
                )
            ).query.pages.values.first().fullUrl
            val msg = MessageChain.Builder()
                .addText("标题: ${response.title} 内容片段如下")
                .addNewLine()
                .addText(response.snippet.extractPlainTextFromHtml())
                .addNewLine(2)
                .addText(fullUrl)
                .build()
            message.reply(msg)
        } catch (_: NoSuchElementException) {
            val msg = MessageChain.Builder()
                .addText("没有查询到指定的wiki页面呢 >>> $title")
                .build()
            message.reply(msg)
        } finally {
            insertActionRecord(CommandAction.Wiki, message.sender.userId, title)
        }
    }
}