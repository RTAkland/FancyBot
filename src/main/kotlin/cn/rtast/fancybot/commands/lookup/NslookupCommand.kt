/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException

class NslookupCommand : BaseCommand() {
    override val commandNames = listOf("/nslookup", "/ns")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val host = args.joinToString(" ")
        try {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("域名: ${host}解析后的IP地址为: ")
                .addText(withContext(Dispatchers.IO) { InetAddress.getByName(host) }.hostAddress)
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        } catch (_: UnknownHostException) {
            message.reply("未知的主机名: $host")
        } catch (e: Exception) {
            message.reply("未知错误: ${e.message}")
        }
    }
}