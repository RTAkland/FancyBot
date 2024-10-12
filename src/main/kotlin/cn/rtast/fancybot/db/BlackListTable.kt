/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.db

import org.jetbrains.exposed.sql.Table

object BlackListTable : Table("blacklist") {
    val id = integer("id").autoIncrement()
    val groupId = long("group_id")
    val operator = long("operator")
    val timestamp = long("timestamp")
    override val primaryKey = PrimaryKey(id)
}

data class BlackListEntity(
    val groupId: Long,
    val operator: Long,
    val timestamp: Long,
)