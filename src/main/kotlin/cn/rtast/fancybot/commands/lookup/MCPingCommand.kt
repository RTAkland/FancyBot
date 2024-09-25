/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.motdpinger.BedrockPing
import cn.rtast.motdpinger.JavaPing
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.SocketException

@CommandDescription("PingMC服务器")
class MCPingCommand : BaseCommand() {
    override val commandNames = listOf("/mcping")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addText("使用方法: /mcping <host>:[port] [java|be]")
                .addNewLine()
                .addText("不添加平台默认Java版~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val platform = if (args.size == 2) args.last() else "java"
        when (platform) {
            "java", "je", "Java", "JAVA" -> {
                val parts = args.first().split(":")
                val port = if (parts.size == 1) {
                    25565
                } else {
                    parts.last().toInt()
                }
                val host = parts.first()
                val response = JavaPing().ping(host, port, 10000)
                if (response == null) {
                    message.reply("无法获取服务器的消息请检查输入是否正确 >>> $host:$port")
                    return
                }
                val msg = MessageChain.Builder()
                response.favicon?.let {
                    msg.addImage(response.favicon!!.replace("data:image/png;base64,", ""), true)
                }
                msg.addText("服务器地址: $host:$port | 服务器类型: Java")
                    .addNewLine()
                    .addText("服务器版本: ${response.version.name}/${response.version.protocol}")
                    .addNewLine()
                    .addText("在线玩家数: ${response.players.online}/${response.players.max}")
                message.reply(msg.build())
            }

            "be", "bedrock", "BE", "Bedrock" -> {
                val parts = args.first().split(":")
                val port = if (parts.size == 1) {
                    19132
                } else {
                    parts.last().toInt()
                }
                val host = parts.first()
                try {
                    val response = BedrockPing().ping(host, port, 10000)
                    val msg = MessageChain.Builder()
                        .addText("服务器地址: $host:$port | 服务器类型: Bedrock")
                        .addNewLine()
                        .addText("服务器版本: ${response.version}/${response.protocolVersion}")
                        .addNewLine()
                        .addText("在线玩家: ${response.onlinePlayers}/${response.maxPlayers}")
                        .addNewLine()
                        .addText("MOTD: ${response.motd}")
                        .addNewLine()
                        .addText(response.subTitle)
                    message.reply(msg.build())
                }catch (_: SocketException) {
                    message.reply("无法从服务器接收Ping结果请检查服务器地址是否正确 >>> $host:$port")
                }
            }

            else -> {
                message.reply("输入错误请在 `java` 和 `be`中选择 >>> $platform")
            }
        }
    }
}