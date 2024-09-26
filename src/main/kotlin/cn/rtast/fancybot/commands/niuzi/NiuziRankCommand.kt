/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.niuziBankManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener


private suspend fun getUserInfo(listener: OneBotListener, groupId: Long, userId: Long): String {
    try {
        val info = listener.getGroupMemberInfo(groupId, userId)
        return info.card ?: info.nickname
    } catch (_: NullPointerException) {
        return userId.toString()
    }
}

class NiuziRankCommand : BaseCommand() {
    override val commandNames = listOf("牛子排行榜")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val allNiuzi = niuziManager.getAllNiuzi().sortedBy { it.length }.reversed()
        val msg = MessageChain.Builder().addText("牛子长度排行榜如下").addNewLine()
        allNiuzi.forEach {
            val userName = getUserInfo(listener, message.groupId, it.userId)
            msg.addText("[$userName] | ${it.length}cm").addNewLine()
        }
        message.reply(msg.build())
    }
}

class NiuziBankRankCommand : BaseCommand() {
    override val commandNames = listOf("牛子富豪榜")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val allNiuziBankAccounts = niuziBankManager.getAllAccount()
            .sortedBy { it.balance }.reversed()
        val msg = MessageChain.Builder().addText("牛子银行富豪榜排行如下").addNewLine()
        allNiuziBankAccounts.forEach {
            val userName = getUserInfo(listener, message.groupId, it.userId)
            msg.addText("[$userName] | ${it.balance}cm").addNewLine()
        }
        message.reply(msg.build())
    }
}