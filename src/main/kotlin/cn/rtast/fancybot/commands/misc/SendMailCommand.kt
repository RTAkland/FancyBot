/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/17
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

@CommandDescription("发送邮件!")
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

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/mail <收件人地址>`即可发送测试邮件~")
            return
        }
        if (message.sender.isAdmin || message.sender.isOwner) {
            val target = args.first()
            this.sendMail(target)
            message.reply("正在发送邮件, 请查收~(可能会出现在垃圾桶中~)")
            insertActionRecord(CommandAction.SendMail, message.sender.userId, target)
        } else {
            message.reply("你不许发邮件")
        }
    }
}