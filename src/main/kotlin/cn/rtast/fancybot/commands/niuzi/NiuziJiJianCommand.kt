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
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("来一起激情击剑吧!")
class NiuziJiJianCommand : BaseCommand() {
    override val commandNames = listOf("击剑", "jj")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`击剑 @xxx`即可对某人进行击剑")
            return
        }

        val target = message.message.find { it.type == ArrayMessageType.at }!!.data
        val targetId = target.qq!!.toLong()
        if (targetId == 0L) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("你的只有一根牛子，他们牛子太多啦你战胜不了他们的~")
                .addNewLine()
                .addText("所以你不能和他们击剑~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        if (message.sender.userId == targetId) {
            message.reply("你不能和自己击剑啊")
            return
        }
        if (!niuziManager.exists(message.sender.userId)) {
            message.reply("你还没有牛子呢, 发送`牛子签到`领取你的牛子吧~")
            return
        }
        if (!niuziManager.exists(targetId)) {
            message.reply("对方还没有牛子呢, 提醒他领取一根牛子吧~")
            return
        }

        if (niuziManager.getUser(message.sender.userId)?.length!! < 0.0) {
            message.reply("你的牛子已经凹进去了没办法进行击剑~发送`牛子签到`来增加你的牛子长度吧~")
            return
        }

        if (niuziManager.getUser(targetId)?.length!! < 0.0) {
            message.reply("他已经没有牛子啦！(他的牛子已经凹进去了！！)你不能和他击剑")
            return
        }
        val result = niuziManager.jijian(message.sender.userId, targetId)
        if (result.first) {
            message.reply("你以已绝对的长度在这场击剑中获胜了~, 你的牛子增加了${result.second}cm")
        } else {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("很不幸你的牛子没能战胜${target.name}, 你的牛子缩短了${result.second}cm")
                .addNewLine()
                .addText("对方的牛子增加了${result.second}cm~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}