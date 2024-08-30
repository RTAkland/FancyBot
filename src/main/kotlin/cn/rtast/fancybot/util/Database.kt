/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.ROOT_PATH
import cn.rtast.fancybot.entity.JrrpTable
import cn.rtast.fancybot.entity.SignTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun initDatabase() {
    Database.connect("jdbc:sqlite:file:$ROOT_PATH/data.sqlite", "org.sqlite.JDBC")
    suspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(JrrpTable)
        SchemaUtils.createMissingTablesAndColumns(SignTable)
    }
}

suspend fun <T> suspendedTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }