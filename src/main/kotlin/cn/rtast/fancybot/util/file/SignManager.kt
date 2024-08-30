/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.jrrp.JrrpRecord
import cn.rtast.fancybot.util.JsonFileHandler
import cn.rtast.fancybot.util.isSameDay
import java.time.Instant
import kotlin.random.Random

class SignManager : JsonFileHandler<List<JrrpRecord>>("sign.json", listOf()) {

    fun isSigned(id: Long): Boolean {
        val allSign = this.readArray<List<JrrpRecord>>()
        val user = allSign.find { it.id == id } ?: return false
        return user.timestamp.isSameDay(Instant.now().epochSecond)
    }

    fun sign(id: Long): Long {
        val allSign = this.readArray<MutableList<JrrpRecord>>()
        val randomPoint = Random.nextLong(0, 101)
        val current = allSign.find { it.id == id } ?: JrrpRecord(id, Instant.now().epochSecond, 0)
        current.points = randomPoint + current.points
        allSign.remove(current)
        allSign.add(current)
        this.write(allSign)
        return randomPoint + current.points
    }

    fun getStatus(id: Long): JrrpRecord? {
        val allSign = this.readArray<List<JrrpRecord>>()
        return allSign.find { it.id == id }
    }
}