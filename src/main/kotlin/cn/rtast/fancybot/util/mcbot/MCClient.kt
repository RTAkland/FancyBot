/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/17
 */


package cn.rtast.fancybot.util.mcbot

import cn.rtast.fancybot.util.Logger
import org.geysermc.mcprotocollib.network.Session
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter
import org.geysermc.mcprotocollib.network.packet.Packet
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol
import org.geysermc.mcprotocollib.protocol.data.game.ClientCommand
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerCombatKillPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket
import java.time.Instant
import java.util.BitSet

class MCClient(
    private val serverHost: String,
    private val serverPort: Int,
    private val botName: String
) {
    private lateinit var client: TcpClientSession
    private val logger = Logger.getLogger<MCClient>()

    fun createClient() {
        val protocol = MinecraftProtocol(botName)
        client = TcpClientSession(serverHost, serverPort, protocol)
    }

    fun runBot(): MCClient {
        client.addListener(object : SessionAdapter() {
            override fun packetReceived(session: Session, packet: Packet) {
                if (packet is ClientboundPlayerPositionPacket) {
                    client.send(ServerboundAcceptTeleportationPacket(packet.teleportId));
                }
                if (packet is ClientboundSystemChatPacket) {
                    logger.trace("系统 >>> {}", packet.content)
                }
                if (packet is ClientboundPlayerChatPacket) {
                    logger.trace("{} >>> {}", packet.name, packet.content)
                }
                if (packet is ClientboundPlayerCombatKillPacket) {
                    logger.info("Bot已死亡自动复活中")
                    client.send(ServerboundClientCommandPacket(ClientCommand.RESPAWN))
                }
            }
        })
        client.connect()
        return this
    }

    private fun sendMessage(content: String) =
        client.send(ServerboundChatPacket(content, Instant.now().toEpochMilli(), 0L, null, 0, BitSet()))

    private fun executeCommand(command: String) = client.send(ServerboundChatCommandPacket(command.substring(1)))


    fun sendChat(text: String) {
        if (text.startsWith("/")) {
            this.executeCommand(text)
        } else {
            this.sendMessage(text)
        }
    }

    fun disconnect() = client.disconnect("Bye~")
}