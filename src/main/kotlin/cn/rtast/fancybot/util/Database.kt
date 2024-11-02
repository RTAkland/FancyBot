/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.db.ActionTable
import cn.rtast.fancybot.db.BlackListTable
import cn.rtast.fancybot.db.JrrpTable
import cn.rtast.fancybot.db.MinecraftAccessTokenTable
import cn.rtast.fancybot.db.NiuziBankTable
import cn.rtast.fancybot.db.NiuziTable
import cn.rtast.fancybot.db.RCONTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun initDatabase() {
    Database.connect("jdbc:h2:file:./data/data.h2;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
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