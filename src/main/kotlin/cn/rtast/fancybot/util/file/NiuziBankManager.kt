/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.entity.db.NiuziBankAccount
import cn.rtast.fancybot.entity.db.NiuziBankTable
import cn.rtast.fancybot.entity.db.NiuziBankTable.balance
import cn.rtast.fancybot.entity.db.NiuziBankTable.interestRate
import cn.rtast.fancybot.entity.db.NiuziBankTable.timestamp
import cn.rtast.fancybot.entity.db.NiuziBankTable.userId
import cn.rtast.fancybot.entity.db.NiuziTable
import cn.rtast.fancybot.util.suspendedTransaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

class NiuziBankManager {

    suspend fun getUser(id: Long): NiuziBankAccount? {
        return suspendedTransaction {
            NiuziBankTable.selectAll().where { userId eq id }.map {
                NiuziBankAccount(
                    it[userId],
                    it[balance],
                    it[interestRate],
                    it[timestamp]
                )
            }.singleOrNull()
        }
    }

    private suspend fun increaseBalance(id: Long, amount: Double) {
        val current = this.getUser(id)
        suspendedTransaction {
            NiuziBankTable.update({ userId eq id }) {
                it[balance] = current?.balance!! + amount
                it[NiuziTable.timestamp] = Instant.now().epochSecond
            }
        }
    }

    private suspend fun decreaseBalance(id: Long, amount: Double) {
        this.increaseBalance(id, -amount)
    }

    suspend fun createBlankAccount(id: Long) {
        suspendedTransaction {
            NiuziBankTable.insert {
                it[userId] = id
                it[balance] = 0.0
                it[timestamp] = Instant.now().epochSecond
                it[interestRate] = 0.54
            }
        }
    }

    suspend fun withdraw(id: Long, amount: Double): NiuziBankAccount {
        suspendedTransaction {
            this.decreaseBalance(id, amount)
        }
        return this.getUser(id)!!
    }

    suspend fun deposit(id: Long, amount: Double): NiuziBankAccount {
        suspendedTransaction {
            this.increaseBalance(id, amount)
        }
        return this.getUser(id)!!
    }

    suspend fun transfer(from: Long, target: Long, amount: Double): NiuziBankAccount? {
        suspendedTransaction {
            this.decreaseBalance(from, amount)
            this.increaseBalance(target, amount)
        }
        return this.getUser(from)
    }
}