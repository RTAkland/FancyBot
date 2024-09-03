/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.entity.db

import org.jetbrains.exposed.sql.Table

object JrrpTable : Table("jrrp") {
    val id = integer("id").autoIncrement()
    val userId = long("userId").uniqueIndex()
    val timestamp = long("timestamp")
    val point = long("point")

    override val primaryKey = PrimaryKey(id)
}

data class JrrpRecord(
    val id: Long,
    val timestamp: Long,
    var points: Long,
)