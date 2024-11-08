/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.util.item

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain

abstract class Item {
    abstract val itemNames: List<String>
    abstract val itemPrice: Double

    abstract suspend fun redeemInGroup(
        message: GroupMessage,
        after: Double,
    ): MessageChain.Builder
}