/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.db

import cn.rtast.fancybot.db.MinecraftAccessTokenTable.timestamp
import cn.rtast.fancybot.db.MinecraftAccessTokenTable.token
import cn.rtast.fancybot.db.MinecraftAccessTokenTable.userId
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant

object MinecraftAccessTokenTable : Table("mc_access_token") {
    val id = integer("id").autoIncrement()
    val token = text("token")
    val userId = long("user_id")
    val timestamp = long("timestamp")
    override val primaryKey = PrimaryKey(id)
}

data class MCAccessTokenEntity(
    val token: String,
    val userId: Long,
    val timestamp: Long,
)

suspend fun insertNewAccessToken(accessToken: String, id: Long) {
    suspendedTransaction {
        MinecraftAccessTokenTable.insert {
            it[token] = accessToken
            it[userId] = id
            it[timestamp] = Instant.now().epochSecond
        }
    }
}

suspend fun getAccessTokenById(id: Long): MCAccessTokenEntity? {
    return suspendedTransaction {
        MinecraftAccessTokenTable.selectAll()
            .where { userId eq id }
            .map {
                MCAccessTokenEntity(
                    it[token],
                    it[userId],
                    it[timestamp]
                )
            }.singleOrNull()
    }
}

suspend fun deleteAccessTokenById(id: Long) {
    suspendedTransaction {
        MinecraftAccessTokenTable.deleteWhere { userId eq id }
    }
}