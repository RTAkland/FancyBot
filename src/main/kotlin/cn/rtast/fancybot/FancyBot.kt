/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.EchoCommand
import cn.rtast.fancybot.commands.JrrpCommand
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.OBMessage
import org.java_websocket.WebSocket

class FancyBot : OBMessage {
    override fun onGroupMessage(websocket: WebSocket, message: GroupMessage, json: String) {
        println(message.rawMessage)
    }
}

fun main() {
    val address = System.getenv("WS_ADDRESS")
    val password = System.getenv("WS_PASSWORD")
    val fancyBot = FancyBot()
    val wsClient = ROneBotFactory.createClient("", "", fancyBot)
    val commandManager = wsClient.commandManager
    commandManager.register(EchoCommand())
    commandManager.register(JrrpCommand())
}