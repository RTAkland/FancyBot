/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


package cn.rtast.fancybot.db

import org.jetbrains.exposed.sql.Table

object ActionTable: Table("action") {
    val id = integer("id").autoIncrement().nullable()
    val action = varchar("action", 255).nullable()
    val timestamp = long("timestamp").nullable()
    val triggerId = long("trigger_id").nullable()
    val result = varchar("result", 255)
    override val primaryKey = PrimaryKey(id)
}