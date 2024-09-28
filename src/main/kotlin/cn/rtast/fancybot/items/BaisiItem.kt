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

class BaisiItem : Item() {
    override val itemNames = listOf("baisi", "白丝", "bs")
    override val itemPrice = 3.0

    override suspend fun redeemInGroup(
        listener: OneBotListener,
        message: GroupMessage,
        after: Double
    ): MessageChain.Builder {
        val url = Http.get<Baisi>("https://v2.api-m.com/api/baisi").data
        val msg = MessageChain.Builder().addImage(url)
        return msg
    }
}