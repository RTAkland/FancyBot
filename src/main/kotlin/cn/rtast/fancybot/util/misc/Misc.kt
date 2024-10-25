/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.util.misc

import cn.rtast.fancybot.*
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.BotInstance
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.util.ob.OneBotAction
import cn.rtast.rob.util.ob.OneBotListener
import java.io.File
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

fun randomBooleanWithProbability(probability: Double): Boolean {
    val randomValue = Random.nextInt(100)
    return randomValue <= (probability * 100)
}

suspend fun OneBotListener.getUserName(action: OneBotAction, groupId: Long, userId: Long): String {
    val info = action.getGroupMemberInfo(groupId, userId)
    return info.card ?: info.nickname
}

fun initCommand() {
    commands.forEach { ROneBotFactory.commandManager.register(it) }
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

fun initItems() {
    items.forEach { itemManager.register(it) }
}

fun initBackgroundTasks(instance: BotInstance) {
    instance.scheduler.scheduleTask({ instance ->
        configManager.admins.forEach {
            instance.action.sendLike(it, 1)
        }
    }, 1.seconds, 3.hours)
}