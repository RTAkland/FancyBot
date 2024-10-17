/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


package cn.rtast.fancybot.db

import org.jetbrains.exposed.sql.Table

object ActionTable : Table("action") {
    val id = integer("id").autoIncrement()
    val action = varchar("action", 255)
    val timestamp = long("timestamp")
    val triggerId = long("trigger_id")
    val result = text("result")
    override val primaryKey = PrimaryKey(id)
}