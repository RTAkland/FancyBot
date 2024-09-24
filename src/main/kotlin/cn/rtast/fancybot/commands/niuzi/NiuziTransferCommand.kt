/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

class NiuziTransferCommand : BaseCommand() {
    override val commandNames = listOf("牛子转账", "转账", "/zz")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`牛子转账 @某人 <长度>` 即可把自己的牛子长度转账给TA")
            return
        }
        val target = message.message.find { it.type == ArrayMessageType.at }!!.data.qq!!.toLong()
        val targetNiuzi = niuziManager.getUser(target)
        if (targetNiuzi == null) niuziManager.createBlankUser(target)
        val transferLength = args.last().toDouble()
        val currentNiuzi = niuziManager.getUser(message.sender.userId)
        if (currentNiuzi == null) {
            message.reply("你没有牛子没办法转账")
            return
        }
        if (currentNiuzi.length <= 0.0) {
            message.reply("你已经没有牛子可以转账了")
            return
        }
        if (currentNiuzi.length < transferLength) {
            message.reply("你的牛子长度不够转账这么多 >>>${transferLength}cm")
            return
        }
        if (transferLength <= 0) {
            message.reply("长度必须大于0!")
            return
        }
        niuziManager.updateLength(message.sender.userId, -transferLength)
        niuziManager.updateLength(target, transferLength)
        message.reply("转账成功, 你的长度减少了: ${transferLength}cm, 对方的长度增加了 ${transferLength}cm")
    }
}