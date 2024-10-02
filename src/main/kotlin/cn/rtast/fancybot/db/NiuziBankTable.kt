/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.db

import org.jetbrains.exposed.sql.Table

object NiuziBankTable : Table("niuzi_bank") {
    val id = long("id").autoIncrement()
    val userId = long("userId").uniqueIndex()
    val timestamp = long("timestamp")
    val balance = double("balance")
    val interestRate = double("interest_rate")
    val nickname = varchar("nickname", 255)
    override val primaryKey = PrimaryKey(id)
}

data class NiuziBankAccount(
    val userId: Long,
    val balance: Double,
    val interestRate: Double,
    val timestamp: Long,
    val nickname: String
)