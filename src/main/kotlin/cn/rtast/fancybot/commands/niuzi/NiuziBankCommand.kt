/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.niuzi

import cn.rtast.fancybot.niuziBankManager
import cn.rtast.fancybot.niuziManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

private suspend fun noAccount(message: GroupMessage) {
    message.reply("你还没有银行账户呢, 发送`创建账户`来创建一个账户吧~")
}

private const val INTEREST_RATE = 0.54

class NiuziBankCommand : BaseCommand() {
    override val commandNames = listOf("牛子银行", "/银行")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val msg = MessageChain.Builder()
            .addText("发送`余额查询` `银行转账` `取牛子` `存牛子` `创建账户` 即可做出对应的操作")
            .addNewLine()
            .addText("银行利率是0.54%")
            .build()
        message.reply(msg)
    }
}

class BankBalanceCommand : BaseCommand() {
    override val commandNames = listOf("余额查询")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val balanceData = niuziBankManager.getUser(message.sender.userId)
        if (balanceData == null) {
            noAccount(message)
            return
        }
        message.reply("你的账户余额还有: ${balanceData.balance}cm")
    }
}

class CreateBankAccountCommand : BaseCommand() {
    override val commandNames = listOf("创建账户")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (niuziBankManager.getUser(message.sender.userId) != null) {
            message.reply("你已经有账户了!")
            return
        }
        niuziBankManager.createBlankAccount(message.sender.userId)
        message.reply("成功创建了一个账户!")
    }
}

class BankTransferCommand : BaseCommand() {
    override val commandNames = listOf("银行转账")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
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
        val result = niuziBankManager.transfer(message.sender.userId, target, amount)
        message.reply("转账成功! 你在银行内剩余的牛子长度为: ${result?.balance}")
    }
}

class WithdrawCommand : BaseCommand() {
    override val commandNames = listOf("取牛子")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
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
        val result = niuziBankManager.withdraw(message.sender.userId, amount)
        niuziManager.updateLength(message.sender.userId, amount + amount * INTEREST_RATE)
        message.reply("取牛子成功, 你现在还剩${result.balance}cm的牛子在银行内")
    }
}

class DepositCommand : BaseCommand() {
    override val commandNames = listOf("存牛子")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val currentBalance = niuziBankManager.getUser(message.sender.userId)
        val niuzi = niuziManager.getUser(message.sender.userId)
        val amount = args.last().toDouble()
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
        val self = niuziManager.updateLength(message.sender.userId, -amount)
        val result = niuziBankManager.deposit(message.sender.userId, amount)
        message.reply("存入成功你身上的牛子还有${self?.length}cm 银行账户内还有: ${result.balance}cm")
    }
}