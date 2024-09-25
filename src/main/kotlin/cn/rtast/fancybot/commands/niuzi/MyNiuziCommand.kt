/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener


@CommandDescription("查询自己牛子的长度")
class MyNiuziCommand : BaseCommand() {
    override val commandNames = listOf("我的牛子", "/mp", "mp")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val niuzi = niuziManager.getUser(message.sender.userId)
        if (niuzi == null) {
            message.reply("你还没有牛子呢, 发送`牛子签到`来领取一根专属于你的牛子吧~")
            return
        }
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("你的牛子长度为: ${niuzi.length}cm")

        val niuziString = when (niuzi.length) {
            in -999.0..0.0 -> "你的牛子已经凹进去了！！！！"
            in 0.1..6.0 -> "小小的也很可爱呢~"
            in 6.1..15.0 -> "好长的牛子!"
            in 15.1..30.0 -> "你的牛子已经快突破天际了！！！"
            else -> "牛子也太长啦把天捅穿啦！！！！！！"
        }
        msg.addText(niuziString)
        listener.sendGroupMessage(message.groupId, msg.build())
    }
}