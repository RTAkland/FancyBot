/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.KFC
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@CommandDescription("今天是疯狂星期四!")
class KFCCommand : BaseCommand() {
    override val commandNames = listOf("/fkxqs", "/kfc")

    companion object {
        fun isThursday(): Boolean {
            val dateTime = LocalDateTime.ofInstant(
                Instant.now(),
                ZoneId.of("Asia/Shanghai")
            )
            return dateTime.dayOfWeek == DayOfWeek.THURSDAY
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (!isThursday()) {
            message.reply("很可惜今天并不是星期四~")
            return
        }
        message.reply(Http.get<KFC>("https://api.shadiao.pro/kfc").data.text)
    }
}