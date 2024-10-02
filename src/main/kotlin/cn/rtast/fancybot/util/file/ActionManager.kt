/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.db.ActionTable
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.insert
import java.time.Instant


suspend fun insertActionRecord(triggerAction: CommandAction, userId: Long, triggerResult: String = "Nothing") {
    suspendedTransaction {
        ActionTable.insert {
            it[action] = triggerAction.action
            it[timestamp] = Instant.now().epochSecond
            it[triggerId] = userId
            it[result] = triggerResult
        }
    }
}