/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.parse.*
import cn.rtast.fancybot.enums.WSType
import cn.rtast.fancybot.util.initDatabase
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.*
import cn.rtast.rob.entity.lagrange.FileEvent
import cn.rtast.rob.entity.lagrange.PokeEvent
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class FancyBot : OneBotListener {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onWebsocketOpenEvent() {
        this.sendPrivateMessage(configManager.startUpNoticeUser, "FancyBot启动完成~")
    }

    override suspend fun onGroupMessage(message: GroupMessage, json: String) {
        val sender = message.sender.nickname
        val senderId = message.sender.userId
        val msg = message.rawMessage
        val groupId = message.groupId
        val messageId = message.messageId
        println("$sender($senderId: $groupId >>> $messageId): $msg")

        if (message.message.any { it.type == ArrayMessageType.face && it.data.id.toString() == "419" }) {
            message.reply("你发牛魔的火车呢, 我直接就是打断")
        }

        if (message.rawMessage.contains("原神")) {
            (0..10).forEach { _ ->
                message.reaction(QQFace.entries.random())
            }
            message.reply("你原神牛魔呢")
        }

        ReverseGIFCommand.callback(message)
        AsciiArtCommand.callback(message)

        if (message.rawMessage.toList().any { it in arrayListOf('*', '-', '/', '+', '=') }) {
            val calculateResult = CalculateCommand.parse(message.rawMessage)
            calculateResult?.let { message.reply(calculateResult) }
        }

        if (message.message.any { it.type == ArrayMessageType.reply }) {  // Image url parse
            val command = message.message.reversed().find { it.type == ArrayMessageType.text }!!.data.text!!
            val replyId = message.message.find { it.type == ArrayMessageType.reply }!!.data.id!!
            if (command.contains("图来") || command.contains("图链")) {
                val getMsg = this.getMessage(replyId.toString().toLong())
                ImageURLCommand.callback(message, getMsg)
            }
        }

        if (message.rawMessage.startsWith("https://github.com/") || message.rawMessage.startsWith("git@github.com:")) {
            GitHubParseCommand.parse(this, message)
        }

        if (message.rawMessage.startsWith("BV") ||
            message.rawMessage.startsWith("https://www.bilibili.com") ||
            message.rawMessage.contains("https://b23.tv/") ||
            message.message.find { it.type == ArrayMessageType.json } != null
        ) {
            BVParseCommand.parse(this, message)
        }

        coroutineScope.launch {
            message.message.forEach {
                if (it.type == ArrayMessageType.image) {
                    val filename = it.data.file!!.split("=").last() + ".png"
                    URI(it.data.file!!).toURL().openConnection().inputStream.use { input ->
                        File("./files/images/$filename").outputStream().use { output ->
                            output.write(input.readBytes())
                        }
                    }
                }
            }
        }
    }

    override suspend fun onGroupMessageRevoke(message: GroupRevokeMessage) {
        if (!configManager.enableAntiRevoke) return
        val msg = MessageChain.Builder()
            .addText("用户: ${message.userId} 被: ${message.operatorId} 撤回了一条消息")
            .addNewLine()
            .addText("使用/revoke ${message.messageId} 来获取被撤回的消息")
            .build()
        this.sendGroupMessage(message.groupId, msg)
    }

    override suspend fun onWebsocketErrorEvent(ex: Exception) {
        ex.printStackTrace()
    }

    override suspend fun onAddFriendRequest(event: AddFriendRequest) {
        event.approve()
    }

    override suspend fun onGroupFileUpload(event: FileEvent) {
        event.saveTo("./files")
    }

    override suspend fun onLeaveEvent(groupId: Long, userId: Long, operator: Long, time: Long) {
        val msg = MessageChain.Builder()
            .addText("有人坐飞船离开了本星系~")
            .addNewLine()
            .addText("QQ: $userId | 操作者: ${if (operator == 0L) "主动退出" else operator}")
            .addNewLine()
            .addText("下次再见吧~~~")
            .build()
        this.sendGroupMessage(groupId, msg)
    }

    override suspend fun onPoke(event: PokeEvent) {
        event.groupId?.let {
            if (event.targetId == configManager.selfId) {
                val msg = MessageChain.Builder()
                    .addAt(event.userId)
                    .addText("你${event.action.first()}牛魔呢")
                    .build()
                this.sendGroupMessage(it, msg)
            }
        }
    }
}

fun initFilesDir() {
    File("./files/images").also { it.mkdirs() }
}

suspend fun main() {
    val fancyBot = FancyBot()
    val workType = configManager.wsType
    val accessToken = configManager.accessToken
    val rob = if (workType == WSType.Client) {
        val address = configManager.wsAddress
        ROneBotFactory.createClient(address, accessToken, fancyBot)
            .also { it.addListeningGroups(*configManager.listeningGroups.toLongArray()) }
    } else {
        val port = configManager.wsPort
        ROneBotFactory.createServer(port, accessToken, fancyBot)
            .also { it.addListeningGroups(*configManager.listeningGroups.toLongArray()) }
    }
    initDatabase()
    initFilesDir()
    val commandManager = rob.commandManager
    commands.forEach { commandManager.register(it) }
    items.forEach { itemManager.register(it) }
}