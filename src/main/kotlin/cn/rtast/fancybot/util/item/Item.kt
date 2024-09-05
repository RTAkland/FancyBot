/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.util.item

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.PrivateMessage
import cn.rtast.rob.util.ob.OBMessage

abstract class Item {
    abstract val itemNames: List<String>
    abstract val itemPrice: Int

    protected open suspend fun redeemInGroup(listener: OBMessage, message: GroupMessage, after: Long) {}

    protected open suspend fun redeemInPrivate(listener: OBMessage, message: PrivateMessage, after: Long) {}

    suspend fun redeemGroup(listener: OBMessage, message: GroupMessage, after: Long) {
        this.redeemInGroup(listener, message, after)
    }

    suspend fun redeemPrivate(listener: OBMessage, message: PrivateMessage, after: Long) {
        this.redeemInPrivate(listener, message, after)
    }
}