/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.EchoCommand
import cn.rtast.fancybot.commands.JrrpCommand
import cn.rtast.fancybot.commands.MusicCommand
import cn.rtast.fancybot.commands.MyPointCommand
import cn.rtast.fancybot.commands.SignCommand
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.OBMessage
import com.sun.net.httpserver.HttpServer
import org.java_websocket.WebSocket
import java.net.InetSocketAddress

class FancyBot : OBMessage {
    override suspend fun onGroupMessage(websocket: WebSocket, message: GroupMessage, json: String) {
        println(message.rawMessage)
    }

    override suspend fun onWebsocketError(webSocket: WebSocket, ex: Exception) {
        println(ex.message)
    }
}

fun createFakeServer() {
    val server = HttpServer.create(InetSocketAddress(8000), 0)
    server.createContext("/") { exchange ->
        val response = "Hello, Kotlin HTTP Server!"
        exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
        val os = exchange.responseBody
        os.write(response.toByteArray())
        os.close()
    }
    server.start()
    println("Server is running on port 8000...")
}

val commands = listOf(
    EchoCommand(),
    JrrpCommand(),
    MusicCommand(),
    SignCommand(),
    MyPointCommand()
)

val configManager = ConfigManager()

fun main() {
    val address = System.getenv("WS_ADDRESS")
    val password = System.getenv("WS_PASSWORD")
    val fancyBot = FancyBot()
    val wsClient = ROneBotFactory.createClient(address, password, fancyBot)
    val commandManager = wsClient.commandManager
    commands.forEach { commandManager.register(it) }
    createFakeServer()
}