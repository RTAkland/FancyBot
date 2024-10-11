/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.db.BlackListEntity
import cn.rtast.fancybot.db.BlackListTable
import cn.rtast.fancybot.db.BlackListTable.operator
import cn.rtast.fancybot.db.BlackListTable.timestamp
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class BlackListManager {

    suspend fun getGroup(groupId: Long) = suspendedTransaction {
        BlackListTable.selectAll()
            .where { BlackListTable.groupId eq groupId }
            .map {
                BlackListEntity(
                    it[BlackListTable.groupId],
                    it[operator],
                    it[timestamp]
                )
            }.singleOrNull()
    }

    suspend fun isBlackListed(groupId: Long): Boolean {
        val group = this.getGroup(groupId)
        return group != null
    }

    suspend fun insertGroup(groupId: Long, operator: Long, timestamp: Long) {
        suspendedTransaction {
            BlackListTable.insert {
                it[BlackListTable.groupId] = groupId
                it[BlackListTable.operator] = operator
                it[BlackListTable.timestamp] = timestamp
            }
        }
    }
}
