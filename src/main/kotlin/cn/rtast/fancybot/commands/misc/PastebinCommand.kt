/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/14
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.pastebin.PastebinPayload
import cn.rtast.fancybot.entity.pastebin.PastebinResponse
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("Pastebin!")
class PastebinCommand : BaseCommand() {
    override val commandNames = listOf("/pastebin", "/pb")

    companion object {
        /**
         * 创建一个pastebin返回一个pair 第一个是原始链接, 第二个是缩短url后的链接
         */
        fun createPastebin(content: String): Pair<String, String> {
            val pastebinPayload = PastebinPayload(content)
            val response = Http.post<PastebinResponse>(
                "$API_RTAST_URL/api/pastebin", pastebinPayload.toJson(),
                params = mapOf("key" to configManager.apiRtastKey)
            )
            val url = "$API_RTAST_URL/api/pp/${response.id}?raw=true"
            return url to url.makeShortLink()
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val shortUrl = createPastebin(content).second
        message.reply(shortUrl)
        insertActionRecord(CommandAction.Pastebin, message.sender.userId, content)
    }
}