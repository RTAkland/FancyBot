/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziBankManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.misc.getUserName
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.util.BaseCommand

@CommandDescription("在群内设置某个成员的牛子银行余额或者现金牛子长度")
class NiuziManagerCommand : BaseCommand() {
    override val commandNames = listOf("管理牛子")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("此命令可以管理成员的牛子或者银行账户")
            return
        }
        println(args)
        if (message.sender.isAdmin || message.sender.isOwner) {
            val target = message.message.find { it.type == ArrayMessageType.at }!!.data.qq!!.toLong()
            val method = args[0]
            val amount = args[1].toDouble()
            when (method) {
                "set", "s", "设置" -> {
                    val type = args[2]
                    when (type) {
                        "yh", "银行" -> {
                            val before = niuziBankManager.getUser(target)
                            niuziBankManager.deposit(
                                target, amount, message.action.getUserName(message.groupId, target)
                            )
                            val after = niuziBankManager.getUser(target)
                            val msg = MessageChain.Builder()
                                .addText("设置成功! 金额: $amount")
                                .addNewLine()
                                .addText("设置前余额: ${before?.balance?.toFloat()} | 设置后余额: ${after?.balance?.toFloat()}")
                                .build()
                            message.reply(msg)
                        }

                        else -> {
                            val before = niuziManager.getUser(target)
                            niuziManager.updateLength(target, amount)
                            val after = niuziManager.getUser(target)
                            val msg = MessageChain.Builder()
                                .addText("设置成功! 金额: $amount")
                                .addNewLine()
                                .addText("设置前长度: ${before?.length?.toFloat()} | 设置后长度: ${after?.length?.toFloat()}")
                                .build()
                            message.reply(msg)
                        }
                    }
                }
            }
        } else {
            message.reply("你没权限呢!")
        }
    }
}