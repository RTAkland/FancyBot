/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("查询别人的牛子长度")
class NiuziQueryCommand : BaseCommand() {
    override val commandNames = listOf("牛子查询")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("输入`牛子查询 @某人`即可查询他的牛子长度")
            return
        }
        val target = message.message.find { it.type == ArrayMessageType.at }?.data?.qq!!.toLong()
        val targetNiuzi = niuziManager.getUser(target)
        if (targetNiuzi == null) {
            message.reply("他还没有牛子")
            return
        }
        message.reply("他的牛子长度为: ${targetNiuzi.length}cm")
    }
}