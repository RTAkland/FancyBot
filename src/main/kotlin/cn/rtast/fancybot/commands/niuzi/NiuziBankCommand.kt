/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands
import cn.rtast.fancybot.niuziBankManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.fancybot.util.misc.getUserName
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain

private suspend fun noAccount(message: GroupMessage) {
    message.reply("你还没有银行账户呢, 发送`创建账户`来创建一个账户吧~")
}

private const val INTEREST_RATE = 0.0054

@CommandDescription("牛子银行根命令")
class NiuziBankCommand : BaseCommand() {
    override val commandNames = listOf("牛子银行")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val niuziCommands = commands.filter {
            it::class.simpleName?.startsWith("Niuzi")!! && !it::class.simpleName?.lowercase()?.contains("redeem")!!
        }.joinToString("\n") { it.commandNames.joinToString(",") { join -> "`$join`" } }
        val msg = MessageChain.Builder()
            .addText("发送")
            .addNewLine()
            .addText(niuziCommands)
            .addNewLine()
            .addText("即可做出对应的操作")
            .addNewLine()
            .addText("银行利率是0.54%")
            .build()
        message.reply(msg)
    }
}

@CommandDescription("查询牛子银行的余额")
class BankBalanceCommand : BaseCommand() {
    override val commandNames = listOf("余额查询")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val balanceData = niuziBankManager.getUser(message.sender.userId)
        if (balanceData == null) {
            noAccount(message)
            return
        }
        message.reply("你的账户余额还有: ${balanceData.balance}cm")
    }
}

@CommandDescription("创建一个空的牛子银行账户")
class CreateBankAccountCommand : BaseCommand() {
    override val commandNames = listOf("创建账户")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (niuziBankManager.getUser(message.sender.userId) != null) {
            message.reply("你已经有账户了!")
            return
        }
        val userInfo = message.action.getGroupMemberInfo(message.groupId, message.sender.userId)
        niuziBankManager.createBlankAccount(message.sender.userId, userInfo.card ?: userInfo.nickname)
        message.reply("成功创建了一个账户!")
    }
}

@CommandDescription("用于牛子银行之间转账")
class BankTransferCommand : BaseCommand() {
    override val commandNames = listOf("银行转账")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val currentUserBalance = niuziBankManager.getUser(message.sender.userId)
        if (currentUserBalance == null) {
            noAccount(message)
            return
        }
        val target = message.message.find { it.type == ArrayMessageType.at }?.data?.qq!!.toLong()
        if (niuziBankManager.getUser(target) == null) {
            message.reply("他还没有银行账户呢, 提醒他创建一个账户吧~(发送`创建账户`)")
            return
        }
        val amount = args.last().toDouble()
        if (amount > currentUserBalance.balance) {
            message.reply("你的牛子长度不够转账!")
            return
        }
        val username = message.action.getUserName(message.groupId, message.sender.userId)
        val result = niuziBankManager.transfer(message.sender.userId, target, amount, username)
        message.reply("转账成功! 你在银行内剩余的牛子长度为: ${result?.balance}")
    }
}

@CommandDescription("牛子银行提现")
class WithdrawCommand : BaseCommand() {
    override val commandNames = listOf("取牛子")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val amount = args.last().toDouble()
        val currentBalance = niuziBankManager.getUser(message.sender.userId)
        val niuzi = niuziManager.getUser(message.sender.userId)
        if (currentBalance == null) {
            noAccount(message)
            return
        }
        if (niuzi == null) {
            message.reply("你还没有认领牛子呢, 发送`牛子签到`来领取一根吧")
            return
        }
        if (amount > currentBalance.balance) {
            message.reply("你的牛子长度不足无法取出 >>> $amount")
            return
        }
        if (amount <= 0) {
            message.reply("取出的长度必须大于0!")
            return
        }
        val username = message.action.getUserName(message.groupId, message.sender.userId)
        val result = niuziBankManager.withdraw(message.sender.userId, amount, username)
        niuziManager.updateLength(message.sender.userId, amount + amount * INTEREST_RATE)
        message.reply("取牛子成功, 你现在还剩${result.balance}cm的牛子在银行内")
    }
}

@CommandDescription("牛子银行存入")
class DepositCommand : BaseCommand() {
    override val commandNames = listOf("存牛子")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val currentBalance = niuziBankManager.getUser(message.sender.userId)
        val niuzi = niuziManager.getUser(message.sender.userId)
        val amount = args.last().toDouble()
        if (amount <= 0) {
            message.reply("存款必须大于0!")
            return
        }
        if (currentBalance == null) {
            noAccount(message)
            return
        }
        if (niuzi == null) {
            message.reply("你还没有认领牛子呢, 发送`牛子签到`来领取一根吧")
            return
        }
        if (amount > niuzi.length) {
            message.reply("你身上的牛子不够用啦, 没办法存进银行! >>> ${niuzi.length}")
            return
        }
        val username = message.action.getUserName(message.groupId, message.sender.userId)
        val self = niuziManager.updateLength(message.sender.userId, -amount)
        val result = niuziBankManager.deposit(message.sender.userId, amount, username)
        message.reply("存入成功你身上的牛子还有${self?.length}cm 银行账户内还有: ${result.balance}cm")
    }
}