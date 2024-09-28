/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/27
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.item.Item
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class NiuziItem : Item() {
    override val itemNames = listOf("牛子", "nz", "niuzi")
    override val itemPrice = 10.0

    override suspend fun redeemInGroup(
        listener: OneBotListener,
        message: GroupMessage,
        after: Double
    ): MessageChain.Builder {
        val before = niuziManager.getUser(message.sender.userId)!!
        val afterNiuzi = niuziManager.updateLength(message.sender.userId, -2.0)!!
        val msg = MessageChain.Builder()
            .addText("你的牛子到账啦, 你的牛子现在有${afterNiuzi.length}cm长呢!")
            .addNewLine()
            .addText("(之前的长度是${before.length}cm!)")
        return msg
    }
}