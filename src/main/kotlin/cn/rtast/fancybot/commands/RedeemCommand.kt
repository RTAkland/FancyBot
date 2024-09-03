/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.Baisi
import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class RedeemCommand : BaseCommand() {
    override val commandNames = listOf("/redeem", "/兑换", "/rdm")

    private val items = mapOf(
        "色图" to 50,
        "黑丝" to 60,
        "白丝" to 70
    )

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val status = signManager.getStatus(message.sender.userId)
        if (status == null) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你还没有签到过呢要签到一次才能兑换哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
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
        val afterStatus = signManager.redeemItem(message.sender.userId, selectedItemPrice)?.points!!
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("兑换成功正在发送奖品中~")
            .addNewLine()
            .addText("你花费了 $selectedItemPrice 点积分来兑换奖品, 还剩 $afterStatus 点积分")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
        when (selectedItemKey) {
            "黑丝" -> {
                val url = Http.get<Baisi>("https://v2.api-m.com/api/heisi").data
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage(url)
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }

            "色图" -> {
                val url = Http.get<Setu>("https://setu.yuban10703.xyz/setu?r18=1&num=1")
                    .data.first().urls.original.replace(
                        "https://i.pximg.net",
                        "https://proxy.rtast.cn/https/i.pixiv.cat"
                    )
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage(url)
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }

            "白丝" -> {
                val url = Http.get<Baisi>("https://v2.api-m.com/api/baisi").data
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addImage(url)
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }
        }
    }
}