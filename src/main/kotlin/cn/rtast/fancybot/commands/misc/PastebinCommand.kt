/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/14
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.PBI_API_URL
import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.pastebin.PastebinPayload
import cn.rtast.fancybot.entity.pastebin.PastebinResponse
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand

@CommandDescription("Pastebin!")
class PastebinCommand : BaseCommand() {
    override val commandNames = listOf("/pastebin", "/pb")

    companion object {
        fun createPastebin(content: String): String {
            val pastebinPayload = PastebinPayload(content)
            val response = Http.post<PastebinResponse>(
                "$API_RTAST_URL/api/pastebin", pastebinPayload.toJson(),
                params = mapOf("key" to configManager.apiRtastKey)
            )
            return "$PBI_API_URL/-/${response.id}"
        }
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("使用`/pb <内容>`或者回复一个消息`/pb`可以快速创建一个pastebin")
            return
        }
        val content = args.joinToString(" ")
        message.reply(createPastebin(content))
        insertActionRecord(CommandAction.Pastebin, message.sender.userId, content)
    }
}