/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.*
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.util.ob.OneBotListener
import java.io.File
import kotlin.random.Random

fun randomBooleanWithProbability(probability: Double): Boolean {
    val randomValue = Random.nextInt(100)
    return randomValue <= (probability * 100)
}

suspend fun OneBotListener.getUserName(groupId: Long, userId: Long): String {
    val info = this.getGroupMemberInfo(groupId, userId)
    return info.card ?: info.nickname
}

fun initCommandAndItem(rob: ROneBotFactory) {
    val commandManager = rob.commandManager
    commands.forEach { commandManager.register(it) }
    items.forEach { itemManager.register(it) }
    tasks.forEach { rob.scheduler.scheduleTask(it.value, 1000L, it.key) }
}

fun initFilesDir() {
    File("$ROOT_PATH/caches/images").also { it.mkdirs() }
    File("$ROOT_PATH/logs").also { it.mkdirs() }
}

fun initSetuIndex() {
    val file = File("$ROOT_PATH/caches/pixiv_index_v3.json")
    if (!file.exists()) {
        file.createNewFile()
        val fileContent = Http.get("$ASSETS_BASE_URL/files/pixiv_index_v3.json")
        file.writeText(fileContent)
    }
}