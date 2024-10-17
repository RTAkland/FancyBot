/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/17
 */


package cn.rtast.fancybot.util.mcbot

import org.geysermc.mcprotocollib.network.tcp.TcpClientSession
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol

class MCClient(
    private val serverHost: String,
    private val serverPort: Int,
    private val botName: String
) {
    fun createClient(): TcpClientSession {
        val protocol = MinecraftProtocol(botName)
        val client = TcpClientSession(serverHost, serverPort, protocol)
        return client
    }
}