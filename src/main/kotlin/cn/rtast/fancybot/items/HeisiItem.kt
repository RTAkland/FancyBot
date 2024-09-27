/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.items

import cn.rtast.fancybot.entity.Baisi
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.item.Item
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class HeisiItem : Item() {
    override val itemNames = listOf("heisi", "黑丝", "hs")
    override val itemPrice = 5.0

    override suspend fun redeemInGroup(listener: OneBotListener, message: GroupMessage, after: Double) {
        val url = Http.get<Baisi>("https://v2.api-m.com/api/heisi").data
        val msg = MessageChain.Builder().addImage(url).build()
        message.reply(msg)
    }
}