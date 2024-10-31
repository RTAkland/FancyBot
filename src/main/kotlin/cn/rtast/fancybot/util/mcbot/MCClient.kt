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
import org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.InteractAction
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundResourcePackPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundDamageEventPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerCombatKillPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerRotPacket
import java.time.Instant
import java.util.BitSet
import java.util.Timer
import java.util.TimerTask
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class MCClient(
    private val serverHost: String,
    private val serverPort: Int,
    private val botName: String,
) {
    private lateinit var client: TcpClientSession
    private val logger = Logger.getLogger<MCClient>()
    private val timer = Timer()
    private val entitiesId = mutableMapOf<Int, Triple<Double, Double, Double>>()

    fun createClient(): TcpClientSession {
        val protocol = MinecraftProtocol(botName)
        client = TcpClientSession(serverHost, serverPort, protocol)
        return client
    }

    fun runBot(): MCClient {
        client.addListener(object : SessionAdapter() {
            override fun packetReceived(session: Session, packet: Packet) {
                when (packet) {
                    is ClientboundResourcePackPushPacket -> {
                        val loadedPacket =
                            ServerboundResourcePackPacket(packet.id, ResourcePackStatus.SUCCESSFULLY_LOADED)
                        session.send(loadedPacket)
                    }

                    is ClientboundPlayerPositionPacket -> {
                        session.send(ServerboundAcceptTeleportationPacket(packet.teleportId))
                    }

                    is ClientboundSystemChatPacket -> {
                        logger.trace("系统 >>> {}", packet.content)
                    }

                    is ClientboundPlayerChatPacket -> {
                        logger.trace("{} >>> {}", packet.name, packet.content)
                    }

                    is ClientboundPlayerCombatKillPacket -> {
                        logger.info("Bot: $botName 死亡, 已自动复活")
                        session.send(ServerboundClientCommandPacket(ClientCommand.RESPAWN))
                    }

                    is ClientboundDamageEventPacket -> {
                        session.send(
                            ServerboundMovePlayerRotPacket(
                                true,
                                Random.nextInt(-90, 90).toFloat(),
                                Random.nextInt(-90, 90).toFloat()
                            )
                        )
                    }

                    is ClientboundMoveEntityPosPacket -> {
                        val entityId = packet.entityId
                        val x = packet.moveX
                        val y = packet.moveY
                        val z = packet.moveZ
                        entitiesId[entityId] = Triple(x, y, z)
                    }
                }
            }
        })
        client.connect()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (client.isConnected) {
                    entitiesId.forEach {
                        val x = it.value.first
                        val y = it.value.second
                        val z = it.value.third
                        val entityId = it.key
                        if (isWithinRange(x, y, z)) {
                            attackEntity(entityId)
                        } else {
                            entitiesId.remove(entityId)
                        }
                    }
                }
            }
        }, 1000L, 1000L)
        return this
    }

    private fun isWithinRange(x: Double, y: Double, z: Double): Boolean {
        val playerX = 0.0
        val playerY = 0.0
        val playerZ = 0.0
        val distance = sqrt((x - playerX).pow(2) + (y - playerY).pow(2) + (z - playerZ).pow(2))
        return distance <= 5
    }


    private fun attackEntity(entityId: Int) {
        val attackPacket =
            ServerboundInteractPacket(entityId, InteractAction.ATTACK, Hand.entries.random(), Random.nextBoolean())
        client.send(attackPacket)
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