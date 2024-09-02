/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.AntiRevokeCommand
import cn.rtast.fancybot.commands.EchoCommand
import cn.rtast.fancybot.commands.FKXQSCommand
import cn.rtast.fancybot.commands.HitokotoCommand
import cn.rtast.fancybot.commands.JrrpCommand
import cn.rtast.fancybot.commands.MusicCommand
import cn.rtast.fancybot.commands.MyPointCommand
import cn.rtast.fancybot.commands.QRCodeCommand
import cn.rtast.fancybot.commands.RedeemCommand
import cn.rtast.fancybot.commands.SignCommand
import cn.rtast.fancybot.entity.enums.WSType
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.fancybot.util.initDatabase
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.GroupRevokeMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import org.java_websocket.WebSocket

class FancyBot : OBMessage {

    override suspend fun onGroupMessage(ws: WebSocket, message: GroupMessage, json: String) {
        println(message.rawMessage)
    }

    override suspend fun onGroupMessageRevoke(ws: WebSocket, message: GroupRevokeMessage) {
        val msg = MessageChain.Builder()
            .addText("用户: ${message.userId} 被: ${message.operatorId} 撤回了一条消息")
            .addNewLine()
            .addText("使用/revoke ${message.messageId} 来获取被撤回的消息")
            .build()
        this.sendGroupMessage(message.groupId, msg)
    }


    override suspend fun onGetGroupMessageResponse(ws: WebSocket, message: GetMessage) {
        if (message.data.id == "revoke") {
            AntiRevokeCommand.getMessageCallback(this, message)
        }
    }

    override suspend fun onWebsocketErrorEvent(ws: WebSocket, ex: Exception) {
        ex.printStackTrace()
    }
}

val commands = listOf(
    EchoCommand(),
    JrrpCommand(),
    MusicCommand(),
    SignCommand(), RedeemCommand(), MyPointCommand(),
    HitokotoCommand(),
    FKXQSCommand(),
    QRCodeCommand(),
    AntiRevokeCommand()
)

val configManager = ConfigManager()

suspend fun main() {
    val fancyBot = FancyBot()
    val workType = configManager.wsType
    val accessToken = configManager.accessToken
    val rob = if (workType == WSType.Client) {
        val address = configManager.wsAddress
        ROneBotFactory.createClient(address, accessToken, fancyBot)
    } else {
        val port = configManager.wsPort
        ROneBotFactory.createServer(port, accessToken, fancyBot)
    }
    val commandManager = rob.commandManager
    commands.forEach { commandManager.register(it) }
    initDatabase()
}