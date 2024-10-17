/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/17
 */


package cn.rtast.fancybot.util.mcbot

import org.geysermc.mcprotocollib.network.Session
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter
import org.geysermc.mcprotocollib.network.packet.Packet
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket
import java.time.Instant
import java.util.BitSet

class MCBot(
    private val content: String = "FancyBot喵~",
    private val continueSendMessage: Boolean = false,
    private val sendDelay: Long = 2000L,
    private val client: TcpClientSession
) : Runnable {

    private fun sendMessage() =
        client.send(ServerboundChatPacket(content, Instant.now().toEpochMilli(), 0L, null, 0, BitSet()))

    override fun run() {
        client.addListener(object : SessionAdapter() {
            override fun packetReceived(session: Session, packet: Packet) {
                if (packet is ClientboundLoginPacket) {
                    if (continueSendMessage) {
                        while (true) {
                            try {
                                sendMessage()
                                Thread.sleep(sendDelay)
                            } catch (_: InterruptedException) {
                                Thread.currentThread().interrupt()
                            }
                        }
                    } else {
                        sendMessage()
                    }
                } else if (packet is ClientboundPlayerPositionPacket) {
                    val p = packet
                    client.send(ServerboundAcceptTeleportationPacket(p.teleportId));
                }
            }
        })
        client.connect()
    }
}