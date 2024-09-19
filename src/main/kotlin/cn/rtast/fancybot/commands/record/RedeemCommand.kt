/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands.record

import cn.rtast.fancybot.itemManager
import cn.rtast.fancybot.signManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class RedeemCommand : BaseCommand() {
    override val commandNames = listOf("/redeem", "/兑换", "/rdm")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送 /rdm <物品名称> 即可兑换哦!")
                .addNewLine()
                .addText("发送 /rdm list 即可查看所有可兑换的物品~")
                .addNewLine()
                .addText("发送 /mp 可以查看自己拥有的点数~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
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
            val priceTable =
                itemManager.items.joinToString("\n") { "[${it.itemNames.joinToString("|")}]:${it.itemPrice}积分" }
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addText(priceTable)
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val selectedItem = itemManager.items.find { it.itemNames.any { any -> any == item } }
        if (selectedItem == null) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("没有找到你想兑换的东西呢")
                .addNewLine()
                .addText("你可以发送 /rdm list来查看所有可兑换的物品")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val selectedItemPrice = selectedItem.itemPrice
        if (selectedItemPrice > status.points) {
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
        selectedItem.redeemGroup(listener, message, afterStatus)
    }
}