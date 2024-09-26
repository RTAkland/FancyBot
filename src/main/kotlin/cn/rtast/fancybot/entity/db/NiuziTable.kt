/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.entity.db

import org.jetbrains.exposed.sql.Table

object NiuziTable : Table("niuzi") {
    val length = double("length")
    val id = long("id").autoIncrement()
    val userId = long("userId").uniqueIndex()
    val timestamp = long("timestamp")
    val nickname = varchar("nickname", 255)
    override val primaryKey = PrimaryKey(id)
}

data class Niuzi(
    val userId: Long,
    val length: Double,
    val timestamp: Long,
    val nickname: String,
)