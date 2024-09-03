/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.MCPingResponse
import cn.rtast.fancybot.util.fromJson
import cn.rtast.motdpinger.MotdPinger
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class MCPingCommand : BaseCommand() {
    override val commandNames = listOf("/mcping", "/mcPing")

    private val pingInstance = MotdPinger()

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addText("使用方法: /mcping <host>:[port]")
                .addNewLine()
                .addText("仅限Java版服务器哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val parts = args.first().split(":")
        val host: String
        val port: Int
        if (parts.size == 2) {
            host = parts[0]
            port = parts[1].toIntOrNull() ?: 25565
        } else {
            host = args.first()
            port = 25565
        }
        try {
            val rawResponse = pingInstance.pingServer(host, port)
            val jsonResponse = rawResponse?.fromJson<MCPingResponse>()!!
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addImage(jsonResponse.favicon, true)
                .addText("服务器地址: $host:$port")
                .addNewLine()
                .addText("协议版本号: ${jsonResponse.version.protocol}/${jsonResponse.version.name}")
                .addNewLine()
                .addText("玩家: ${jsonResponse.players.online}/${jsonResponse.players.max}")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        } catch (_: Exception) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("ping失败~")
                .addNewLine()
                .addText("检查一下地址或者端口是否正确吧~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}