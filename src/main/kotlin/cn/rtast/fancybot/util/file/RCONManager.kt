/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/13
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.db.MinecraftAccessTokenTable
import cn.rtast.fancybot.db.RCONEntity
import cn.rtast.fancybot.db.RCONTable
import cn.rtast.fancybot.db.RCONTable.host
import cn.rtast.fancybot.db.RCONTable.password
import cn.rtast.fancybot.db.RCONTable.port
import cn.rtast.fancybot.util.suspendedTransaction
import cn.rtast.rcon.RCon
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class RCONManager {

    suspend fun getRcon(userId: Long, name: String): RCONEntity? {
        return suspendedTransaction {
            RCONTable.selectAll().where { (RCONTable.userId eq userId) and (RCONTable.name eq name) }
                .map { RCONEntity(it[host], it[port], it[RCONTable.userId], it[RCONTable.name], it[password]) }
        }.singleOrNull()
    }

    suspend fun getAllRconsById(userId: Long): List<RCONEntity> {
        return suspendedTransaction {
            RCONTable.selectAll().where { (RCONTable.userId eq userId) }
                .map { RCONEntity(it[host], it[port], it[RCONTable.userId], it[RCONTable.name], it[password]) }
        }
    }

    suspend fun executeCommand(userId: Long, name: String, command: String): String {
        val rconData = getRcon(userId, name)!!
        val rcon = RCon(rconData.host, rconData.port)
        rcon.authenticate(rconData.password)
        val result = rcon.executeCommand(command).body
        rcon.close()
        return result
    }

    suspend fun insertConfig(userId: Long, name: String, host: String, port: Int, password: String) {
        suspendedTransaction {
            RCONTable.insert {
                it[RCONTable.userId] = userId
                it[RCONTable.name] = name
                it[RCONTable.host] = host
                it[RCONTable.port] = port
                it[RCONTable.password] = password
            }
        }
    }

    suspend fun removeRRCON(userId: Long, name: String): Int {
        return suspendedTransaction {
            MinecraftAccessTokenTable.deleteWhere { (RCONTable.userId eq userId) and (RCONTable.name eq name) }
        }
    }
}