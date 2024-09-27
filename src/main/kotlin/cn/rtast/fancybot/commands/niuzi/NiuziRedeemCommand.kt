/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.itemManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("使用牛子长度兑换物品")
class NiuziRedeemCommand : BaseCommand() {
    override val commandNames = listOf("/rdm")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("发送 /rdm <物品名称> 即可兑换哦!")
                .addNewLine()
                .addText("发送 /rdm list 即可查看所有可兑换的物品~")
                .addNewLine()
                .addText("发送 `我的牛子` 可以查看自己的牛子长度~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val status = niuziManager.getUser(message.sender.userId)
        if (status == null) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("你还没有签到过呢要签到一次才能兑换哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val item = args.first()
        if (item == "list" || item == "列表") {
            val priceTable =
                itemManager.items.joinToString("\n")
                { "[${it.itemNames.joinToString("|")}]:${it.itemPrice}cm" }
            message.reply(priceTable)
            return
        }
        val selectedItem = itemManager.items.find { it.itemNames.any { any -> any == item } }
        if (selectedItem == null) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("没有找到你想兑换的东西呢")
                .addNewLine()
                .addText("你可以发送 /rdm list来查看所有可兑换的物品")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val selectedItemPrice = selectedItem.itemPrice
        if (selectedItemPrice > status.length) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("你的牛子长度不够兑换这个物品呢~")
                .addNewLine()
                .addText("你有: ${status.length}cm, 兑换需要: $selectedItemPrice cm~\"")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val afterStatus = niuziManager.redeemItem(message.sender.userId, selectedItemPrice)?.length!!
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText("兑换成功正在发送奖品中~")
            .addNewLine()
            .addText("你花费了${selectedItemPrice}cm来兑换奖品, 还剩${afterStatus}cm")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
        selectedItem.redeemGroup(listener, message, afterStatus)
    }
}