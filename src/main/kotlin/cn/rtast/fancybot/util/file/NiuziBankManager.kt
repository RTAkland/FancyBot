/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.db.NiuziBankAccount
import cn.rtast.fancybot.db.NiuziBankTable
import cn.rtast.fancybot.db.NiuziBankTable.balance
import cn.rtast.fancybot.db.NiuziBankTable.interestRate
import cn.rtast.fancybot.db.NiuziBankTable.nickname
import cn.rtast.fancybot.db.NiuziBankTable.timestamp
import cn.rtast.fancybot.db.NiuziBankTable.userId
import cn.rtast.fancybot.db.NiuziTable
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
                    it[timestamp],
                    it[nickname]
                )
            }.singleOrNull()
        }
    }

    private suspend fun increaseBalance(id: Long, amount: Double, username: String) {
        val current = this.getUser(id)
        suspendedTransaction {
            NiuziBankTable.update({ userId eq id }) {
                it[balance] = current?.balance!! + amount
                it[NiuziTable.timestamp] = Instant.now().epochSecond
                it[nickname] = username
            }
        }
    }

    private suspend fun decreaseBalance(id: Long, amount: Double, username: String) {
        this.increaseBalance(id, -amount, username)
    }

    suspend fun createBlankAccount(id: Long, username: String) {
        suspendedTransaction {
            NiuziBankTable.insert {
                it[userId] = id
                it[balance] = 0.0
                it[timestamp] = Instant.now().epochSecond
                it[interestRate] = 0.54
                it[nickname] = username
            }
        }
    }

    suspend fun withdraw(id: Long, amount: Double, username: String): NiuziBankAccount {
        suspendedTransaction {
            this.decreaseBalance(id, amount, username)
        }
        return this.getUser(id)!!
    }

    suspend fun deposit(id: Long, amount: Double, username: String): NiuziBankAccount {
        suspendedTransaction {
            this.increaseBalance(id, amount, username)
        }
        return this.getUser(id)!!
    }

    suspend fun transfer(from: Long, target: Long, amount: Double, username: String): NiuziBankAccount? {
        suspendedTransaction {
            this.decreaseBalance(from, amount, username)
            this.increaseBalance(target, amount, username)
        }
        return this.getUser(from)
    }

    suspend fun getAllAccount(): List<NiuziBankAccount> =
        suspendedTransaction {
            NiuziBankTable.selectAll()
                .map { NiuziBankAccount(it[userId], it[balance], it[interestRate], it[timestamp], it[nickname]) }
        }
}