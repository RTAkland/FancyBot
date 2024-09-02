/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.ADMINS
import cn.rtast.fancybot.entity.Heisi
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.file.SignManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.UserRole
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

private val signManager = SignManager()

class SignCommand : BaseCommand() {
    override val commandNames = listOf("/签到", "/sign")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (signManager.isSigned(message.sender.userId)) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你今天已经签到过啦, 明天再来吧~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val randomPoint = signManager.sign(message.sender.userId)
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("你获得了 $randomPoint 点分数!")
            .addNewLine()
            .addText("发送‘/我的点数’ 或者 ‘/mp’ 即可查看当前的点数")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}

class MyPointCommand : BaseCommand() {
    override val commandNames = listOf("/我的点数", "/mp")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isNotEmpty() && (message.sender.role == UserRole.admin || message.sender.userId in ADMINS)) {
            val targetId = args.first().toLong()
            val targetData = signManager.getStatus(targetId)
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)

            if (targetData == null) {
                msg.addText("用户不存在")
            } else {
                msg.addText("用户: ${targetData.id} 的点数有: ${targetData.points}")
            }
            listener.sendGroupMessage(message.groupId, msg.build())
            return
        }
        val status = signManager.getStatus(message.sender.userId)
        if (status == null) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你还没有签到过呢! 先发送‘/签到’再来查询吧~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        } else {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你当前的点数为: ${status.points}")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}

private val items = mapOf(
    "色图" to 50,
    "黑丝" to 60,
    "白丝" to 70
)

class RedeemCommand : BaseCommand() {
    override val commandNames = listOf("/redeem", "/兑换", "/rdm")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val status = signManager.getStatus(message.sender.userId)
        val item = args.first()
        if (item == "list" || item == "列表") {
            val priceTable = items.entries.joinToString("\n") { (key, value) -> "名称: $key=$value 积分" }
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addText(priceTable)
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val selectedItemKey = items.keys.find { it == item }
        if (selectedItemKey == null) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("没有找到你想兑换的东西呢")
                .addNewLine()
                .addText("你可以输入/兑换 list来查看所有可兑换的物品")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val selectedItemPrice = items[item]!!
        if (selectedItemPrice > status?.points!!) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你的积分不够兑换这个物品呢~")
                .addNewLine()
                .addText("你有: ${status.points}个积分, 兑换需要: $selectedItemPrice 个积分~\"")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        signManager.redeemItem(message.sender.userId, selectedItemPrice)
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("兑换成功正在发送奖品中~")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
        when (selectedItemKey) {
            "黑丝" -> {
                val url = Http.get<Heisi>("https://v2.api-m.com/api/heisi").data
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage(url)
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }

            "色图" -> {
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage("https://moe.jitsu.top/img/?sort=r18&size=small")
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }

            "白丝" -> {
                val url = Http.get<Heisi>("https://v2.api-m.com/api/baisi").data
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage(url)
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }
        }
    }
}