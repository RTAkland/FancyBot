/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.entity.Setu
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class SetuItem: Item() {
    override val itemNames = listOf("setu", "色图", "st")
    override val itemPrice = 80

    override suspend fun redeemInGroup(listener: OBMessage, message: GroupMessage, after: Long) {
        val url = Http.get<Setu>("https://api.rtast.cn/api/setu").urls.large
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage(url)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}