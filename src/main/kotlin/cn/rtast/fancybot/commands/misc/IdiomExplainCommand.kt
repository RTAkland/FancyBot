/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.apispace.IdiomExplainResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class IdiomExplainCommand : BaseCommand() {
    override val commandNames = listOf("成语解释")

    private val apiSpaceUrl = "https://eolink.o.apispace.com/cyudac/api/v1/idioms/SearchIdiomsByPinyin/search"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`成语解释 <成语>`就能解释这个成语的意思了")
            return
        }
        try {
            val idiom = args.joinToString(" ").trim()
            val response = Http.get<IdiomExplainResponse>(
                apiSpaceUrl,
                mapOf("name" to idiom, "search_type" to "name"),
                mapOf("X-APISpace-Token" to configManager.apiSpaceKey)
            ).data.first()
            val msg = MessageChain.Builder()
                .addText("成语: ${response.name} 的解释如下")
                .addNewLine()
                .addText(response.explanation)
                .addNewLine(2)
                .addText(response.provenance)
                .addNewLine(2)
                .addText(response.sound)
                .build()
            message.reply(msg)
        } catch (_: Exception) {
            message.reply("这个词似乎不是一个成语呢~~")
        }
    }
}