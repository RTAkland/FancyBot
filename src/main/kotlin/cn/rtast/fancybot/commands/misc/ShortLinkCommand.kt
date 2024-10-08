/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/22
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.shortlink.ShortLinkPayload
import cn.rtast.fancybot.entity.shortlink.ShortLinkResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("生成短链接")
class ShortLinkCommand : BaseCommand() {
    override val commandNames = listOf("/s")


    companion object {
        fun String.makeShortLink(): String {
            val shortLink = Http.post<ShortLinkResponse>(
                "$API_RTAST_URL/short_link",
                ShortLinkPayload(this).toJson()
            )
            return "$API_RTAST_URL/s?rnd_id=${shortLink.id}"
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/s <url>`来生成短链接`不要放入一些奇怪的链接哦~")
            return
        }
        val target = args.first().encodeToBase64()
        val shortLink = target.makeShortLink()
        message.reply(shortLink)
    }
}