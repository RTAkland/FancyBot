/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.niuziBankManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("查询所有人的牛子的排行榜")
class NiuziRankCommand : BaseCommand() {
    override val commandNames = listOf("牛子排行榜", "牛子排行")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val allNiuzi = niuziManager.getAllNiuzi().sortedBy { it.length }.reversed()
        val msg = MessageChain.Builder().addText("牛子长度排行榜如下").addNewLine()
        allNiuzi.forEach {
            msg.addText("[${it.nickname}] | ${it.length}cm").addNewLine()
        }
        message.reply(msg.build())
    }
}

@CommandDescription("查询所有人的银行账户余额排行榜")
class NiuziBankRankCommand : BaseCommand() {
    override val commandNames = listOf("牛子富豪榜", "富豪榜")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val allNiuziBankAccounts = niuziBankManager.getAllAccount()
            .sortedBy { it.balance }.reversed()
        val msg = MessageChain.Builder().addText("牛子银行富豪榜排行如下").addNewLine()
        allNiuziBankAccounts.forEach {
            msg.addText("[${it.nickname}] | ${it.balance}cm").addNewLine()
        }
        message.reply(msg.build())
    }
}