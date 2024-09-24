/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.commands.record

import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class NiuziSignCommand : BaseCommand() {
    override val commandNames = listOf("牛子签到", "签到")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (niuziManager.isSigned(message.sender.userId)) {
            message.reply("你今天已经对你的牛子使用了签到啦,明天再来吧~")
            return
        }
        val afterStatus = niuziManager.sign(message.sender.userId)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("签到成功~")
            .addNewLine()
            .addText("你的牛子增加了${afterStatus.first}cm~ 总长度为: ${afterStatus.second?.length}")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}

class JiJianCommand : BaseCommand() {
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

class NiuziQueryCommand : BaseCommand() {
    override val commandNames = listOf("牛子查询", "nq")

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