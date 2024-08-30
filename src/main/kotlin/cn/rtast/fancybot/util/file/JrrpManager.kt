/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.jrrp.JrrpRecord
import cn.rtast.fancybot.util.JsonFileHandler
import cn.rtast.fancybot.util.isSameDay
import java.time.Instant
import kotlin.random.Random

class JrrpManager : JsonFileHandler<List<JrrpRecord>>("jrrp.json", listOf()) {

    fun isJrrped(id: Long): Boolean {
        val allJrrp = this.readArray<List<JrrpRecord>>()
        val user = allJrrp.find { it.id == id } ?: return false
        return user.timestamp.isSameDay(Instant.now().epochSecond)
    }

    fun jrrp(id: Long): Int {
        val allJrrp = this.readArray<MutableList<JrrpRecord>>()
        allJrrp.removeAll { it.id == id }
        allJrrp.add(JrrpRecord(id, Instant.now().epochSecond, 0))
        this.write(allJrrp)
        return Random.nextInt(0, 101)
    }
}