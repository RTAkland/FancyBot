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
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("Pastebin!")
class PastebinCommand : BaseCommand() {
    override val commandNames = listOf("/pastebin", "/pb")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val pastebinPayload = PastebinPayload(content)
        val response = Http.post<PastebinResponse>(
            "$API_RTAST_URL/api/pastebin", pastebinPayload.toJson(),
            params = mapOf("key" to configManager.apiRtastKey)
        )
        message.reply("$API_RTAST_URL/api/pp/${response.id}?raw=true".makeShortLink())
    }
}