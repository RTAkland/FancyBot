/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.util.item

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.OneBotListener

abstract class Item {
    abstract val itemNames: List<String>
    abstract val itemPrice: Double

    protected open suspend fun redeemInGroup(listener: OneBotListener, message: GroupMessage, after: Double) {}

    suspend fun redeemGroup(listener: OneBotListener, message: GroupMessage, after: Double) {
        this.redeemInGroup(listener, message, after)
    }
}