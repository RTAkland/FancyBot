/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.db.Niuzi
import cn.rtast.fancybot.entity.db.NiuziTable
import cn.rtast.fancybot.entity.db.NiuziTable.length
import cn.rtast.fancybot.entity.db.NiuziTable.timestamp
import cn.rtast.fancybot.entity.db.NiuziTable.userId
import cn.rtast.fancybot.util.isSameDay
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import kotlin.random.Random

class NiuziManager {
    suspend fun getUser(id: Long): Niuzi? {
        return suspendedTransaction {
            NiuziTable.selectAll().where { userId eq id }.map {
                Niuzi(
                    it[userId],
                    it[length],
                    it[timestamp]
                )
            }.singleOrNull()
        }
    }

    suspend fun isSigned(id: Long): Boolean {
        val record = getUser(id)
        return record?.timestamp?.isSameDay(Instant.now().epochSecond) ?: false
    }

    suspend fun sign(id: Long): Pair<Double, Niuzi?> {
        val randomLength = Random.nextDouble(-5.0, 10.0)
        if (getUser(id) == null) {
            suspendedTransaction {
                NiuziTable.insert {
                    it[userId] = id
                    it[timestamp] = Instant.now().epochSecond
                    it[length] = 0.0
                }
            }
        }
        val record = getUser(id)
        suspendedTransaction {
            NiuziTable.update({ userId eq id }) {
                it[length] = record?.length!! + randomLength
                it[timestamp] = Instant.now().epochSecond
            }
        }
        return randomLength to this.getUser(id)
    }

    suspend fun updateLength(id: Long, newLength: Double) {
        val current = getUser(id)
        suspendedTransaction {
            NiuziTable.update({ userId eq id }) {
                it[length] = current?.length!! + newLength
            }
        }
    }

    suspend fun exists(userId: Long): Boolean {
        return getUser(userId) != null
    }

    suspend fun jijian(fromUser: Long, targetUser: Long): Pair<Boolean, Double> {
        val fromUserStatus = getUser(fromUser)!!
        val targetUserStatus = getUser(targetUser)!!
        val randomLength = Random.nextDouble(1.0, 10.0)
        val success = Random.nextBoolean()
        if (success) {
            this.updateLength(fromUser, randomLength)
            this.updateLength(targetUser, -randomLength)
        } else {
            this.updateLength(fromUser, -randomLength)
            this.updateLength(targetUser, randomLength)
        }
        return success to randomLength
    }
}