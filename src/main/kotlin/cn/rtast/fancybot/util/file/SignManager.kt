/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.JrrpRecord
import cn.rtast.fancybot.entity.SignTable
import cn.rtast.fancybot.entity.SignTable.point
import cn.rtast.fancybot.entity.SignTable.timestamp
import cn.rtast.fancybot.entity.SignTable.userId
import cn.rtast.fancybot.util.suspendedTransaction
import cn.rtast.fancybot.util.isSameDay
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import kotlin.random.Random

class SignManager {

    private suspend fun getRecord(id: Long): JrrpRecord? {
        return suspendedTransaction {
            SignTable.selectAll().where { userId eq id }.map {
                JrrpRecord(
                    it[userId],
                    it[timestamp],
                    it[point],
                )
            }.singleOrNull()
        }
    }

    suspend fun isSigned(id: Long): Boolean {
        val record = getRecord(id)
        return if (record != null) {
            record.timestamp.isSameDay(Instant.now().epochSecond)
        } else {
            false
        }
    }

    suspend fun sign(id: Long): Long {
        val randomPoint = Random.nextLong(0, 101)
        if (getRecord(id) == null) {
            suspendedTransaction {
                SignTable.insert {
                    it[userId] = id
                    it[timestamp] = Instant.now().epochSecond
                    it[point] = 0
                }
            }
        }
        val record = getRecord(id)
        suspendedTransaction {
            SignTable.update({ userId eq id }) {
                it[point] = record?.points!! + randomPoint
                it[timestamp] = Instant.now().epochSecond
            }
        }
        return randomPoint
    }

    suspend fun getStatus(id: Long): JrrpRecord? {
        return getRecord(id)
    }
}