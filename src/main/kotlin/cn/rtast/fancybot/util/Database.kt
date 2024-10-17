/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.ROOT_PATH
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.db.ActionTable
import cn.rtast.fancybot.db.BlackListTable
import cn.rtast.fancybot.db.JrrpTable
import cn.rtast.fancybot.db.MinecraftAccessTokenTable
import cn.rtast.fancybot.db.NiuziBankTable
import cn.rtast.fancybot.db.NiuziTable
import cn.rtast.fancybot.db.RCONTable
import cn.rtast.fancybot.enums.DatabaseType
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun initDatabase() {
    val host = configManager.dbConfig.host
    val port = configManager.dbConfig.port
    val user = configManager.dbConfig.user
    val password = configManager.dbConfig.password
    val database = configManager.dbConfig.database
    if (configManager.dbConfig.type == DatabaseType.MySQL) {
        Database.connect("jdbc:mysql://${host}:${port}/$database", "com.mysql.cj.jdbc.Driver", user, password)
    } else if (configManager.dbConfig.type == DatabaseType.PostgreSQL) {
        Database.connect("jdbc:postgresql://$host:$port/$database", "org.postgresql.Driver", user, password)
    } else {
        Database.connect("jdbc:sqlite:file:$ROOT_PATH/data.sqlite", "org.sqlite.JDBC")
    }
    suspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(JrrpTable)
        SchemaUtils.createMissingTablesAndColumns(NiuziBankTable)
        SchemaUtils.createMissingTablesAndColumns(NiuziTable)
        SchemaUtils.createMissingTablesAndColumns(ActionTable)
        SchemaUtils.createMissingTablesAndColumns(BlackListTable)
        SchemaUtils.createMissingTablesAndColumns(MinecraftAccessTokenTable)
        SchemaUtils.createMissingTablesAndColumns(RCONTable)
    }
}

suspend fun <T> suspendedTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }