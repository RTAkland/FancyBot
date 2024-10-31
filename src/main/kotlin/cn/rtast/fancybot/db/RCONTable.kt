/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/13
 */


package cn.rtast.fancybot.db

import org.jetbrains.exposed.sql.Table

object RCONTable : Table("rcon") {
    val id = integer("id").autoIncrement()
    val host = varchar("host", 50)
    val port = integer("port")
    val userId = long("user_id")
    val password = varchar("password", 128)
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

data class RCONEntity(
    val host: String,
    val port: Int,
    val userId: Long,
    val name: String,
    val password: String,
)

