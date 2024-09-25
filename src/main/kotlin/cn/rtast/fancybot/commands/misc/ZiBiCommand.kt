/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/18
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.PrivateMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

val users = mutableMapOf<Long, Long>()

@CommandDescription("自闭去吧你")
class ZiBiCommand : BaseCommand() {
    override val commandNames = listOf("/自闭", "自闭")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        try {
            users.remove(message.sender.userId)
            val duration = if (args.isEmpty()) 1 else args.first().toInt()
            message.sender.ban(duration * 60)
            val msg = MessageChain.Builder()
                .addText("你先自闭${duration}分钟吧~")
                .addNewLine()
                .addText("私聊发送`我想开了`即可取消禁言~")
                .build()
            message.reply(msg)
            users[message.sender.userId] = message.groupId
        } catch (_: Exception) {
            val msg = MessageChain.Builder()
                .addText("你输入有误所以我决定让你自闭1天~")
                .build()
            message.reply(msg)
            message.sender.ban(86400)
        }
    }
}

@CommandDescription("我好像突然想开了")
class UnsetZiBiCommand : BaseCommand() {
    override val commandNames = listOf("我想开了")

    override suspend fun executePrivate(listener: OneBotListener, message: PrivateMessage, args: List<String>) {
        if (!users.any { it.key == message.sender.userId }) {
            val msg = MessageChain.Builder()
                .addText("你没有在自闭列表中~")
                .build()
            message.reply(msg)
            return
        }
        listener.setGroupBan(users.getValue(message.sender.userId), message.sender.userId, 0)
        val msg = MessageChain.Builder()
            .addText("祝你天天开心不要自闭~~")
            .build()
        message.reply(msg)
        users.remove(message.sender.userId)
    }
}