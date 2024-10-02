/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.db.JrrpRecord
import cn.rtast.fancybot.db.JrrpTable
import cn.rtast.fancybot.util.isSameDay
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import kotlin.random.Random

class JrrpManager {

    private suspend fun getRecord(id: Long): JrrpRecord? {
        return suspendedTransaction {
            JrrpTable.selectAll().where { JrrpTable.userId eq id }.map {
                JrrpRecord(
                    it[JrrpTable.userId],
                    it[JrrpTable.timestamp],
                    it[JrrpTable.point],
                )
            }.singleOrNull()
        }
    }

    suspend fun isJrrped(id: Long): Boolean {
        val record = getRecord(id)
        return record?.timestamp?.isSameDay(Instant.now().epochSecond) ?: false
    }

    suspend fun jrrp(id: Long): Long {
        val randomPoint = Random.nextLong(0, 101)
        if (getRecord(id) == null) {
            suspendedTransaction {
                JrrpTable.insert {
                    it[userId] = id
                    it[timestamp] = Instant.now().epochSecond
                    it[point] = 0
                }
            }
        }
        val record = getRecord(id)
        suspendedTransaction {
            JrrpTable.update({ JrrpTable.userId eq id }) {
                it[point] = record?.points!! + randomPoint
                it[timestamp] = Instant.now().epochSecond
            }
        }
        return randomPoint
    }
}