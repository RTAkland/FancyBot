/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/17
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.configManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

class SendMailCommand : BaseCommand() {
    override val commandNames = listOf("/mail")

    private val smtpHost = configManager.smtpHost
    private val smtpPort = configManager.smtpPort
    private val smtpUser = configManager.smtpUser
    private val smtpPassword = configManager.smtpPassword
    private val smtpFromAddress = configManager.smtpFromAddress
    private val mailer = MailerBuilder.withSMTPServer(smtpHost, smtpPort, smtpUser, smtpPassword)
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .clearEmailValidator()
        .async()
        .buildMailer()

    private fun sendMail(target: String) {
        val email = EmailBuilder.startingBlank()
            .from(smtpFromAddress)
            .to(target)
            .withSubject("Test email from FancyBot")
            .withPlainText("Test email~")
            .buildEmail()
        this.mailer.sendMail(email)
    }

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`/mail <收件人地址>`即可发送测试邮件~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        if (message.sender.userId !in configManager.admins) {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("你不许发邮件")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val target = args.first()
        this.sendMail(target)
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("正在发送邮件, 请查收~(可能会出现在垃圾桶中~)")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}