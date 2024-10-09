/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/9
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@CommandDescription("高考倒计时")
class GaoKaoDaysRemainCommand : BaseCommand() {
    override val commandNames = listOf("高考倒计时")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val gaoKaoDate = LocalDate.of(LocalDate.now().year, 6, 7)
        val currentDate = LocalDate.now()
        val adjustedGaoKaoDate = if (currentDate.isAfter(gaoKaoDate)) {
            gaoKaoDate.plusYears(1)
        } else gaoKaoDate
        val daysBetween = ChronoUnit.DAYS.between(currentDate, adjustedGaoKaoDate)
        message.reply("高考倒计时: ${daysBetween}天")
    }
}