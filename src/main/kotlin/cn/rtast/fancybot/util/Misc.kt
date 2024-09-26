/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.util

import cn.rtast.rob.util.ob.OneBotListener
import kotlin.random.Random

fun randomBooleanWithProbability(probability: Double): Boolean {
    val randomValue = Random.nextInt(100)
    return randomValue <= (probability * 100)
}

suspend fun OneBotListener.getUserName(groupId: Long, userId: Long): String {
    val info = this.getGroupMemberInfo(groupId, userId)
    return info.card ?: info.nickname
}