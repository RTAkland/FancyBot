/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.misc.ReactionCommand
import cn.rtast.fancybot.commands.misc.ScanQRCodeCommand
import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.commands.parse.*
import cn.rtast.fancybot.enums.WSType
import cn.rtast.fancybot.util.*
import cn.rtast.fancybot.util.file.getFileType
import cn.rtast.fancybot.util.misc.ImageBed
import cn.rtast.fancybot.util.misc.convertToDate
import cn.rtast.fancybot.util.misc.initCommandAndItem
import cn.rtast.fancybot.util.misc.initFilesDir
import cn.rtast.fancybot.util.misc.initSetuIndex
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.*
import cn.rtast.rob.entity.lagrange.FileEvent
import cn.rtast.rob.entity.lagrange.PokeEvent
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import cn.rtast.rob.util.ob.asNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class FancyBot : OneBotListener {

    private val logger = Logger.getLogger<FancyBot>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val githubRegex = Regex("""github\.com/([^/]+)/([^/]+)""")

    override suspend fun onWebsocketOpenEvent() {
        this.sendPrivateMessage(configManager.noticeUser, "FancyBot启动完成~")
    }

    override suspend fun onGroupMessage(message: GroupMessage, json: String) {
        val sender = message.sender.nickname
        val senderId = message.sender.userId
        val msg = message.rawMessage
        val groupId = message.groupId
        val messageId = message.messageId
        logger.info("$sender($senderId: $groupId >>> $messageId): $msg")
        logger.trace("$sender($senderId: $groupId >>> $messageId: $json")

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
        ScanQRCodeCommand.callback(message)
        BiliVideoParseCommand.parse(this, message)
        BiliUserParseCommand.parse(this, message)

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
            if (command.contains("reaction")) {
                val getMsg = this.getMessage(replyId.toString().toLong())
                ReactionCommand.reaction(this, message, getMsg.messageId)
            }
            if (command.contains("图床")) {
                val getMsg = this.getMessage(replyId.toString().toLong())
                val imagesUrl = ImageURLCommand.getImageUrl(getMsg)
                if (imagesUrl.isEmpty()) {
                    message.reply("这个消息里没有图片呢!")
                } else {
                    try {
                        if (imagesUrl.size == 1) {
                            val imageByteArray = imagesUrl.first().toURL().readBytes()
                            val imageFileType = imageByteArray.getFileType()
                            val imageBedUrl = ImageBed.upload(imageByteArray, imageFileType)
                            val msg = MessageChain.Builder()
                                .addText(imageBedUrl.makeShortLink())
                                .addNewLine(2)
                                .addText(imageBedUrl)
                                .build()
                            message.reply(msg)
                        } else {
                            val messages = mutableListOf<MessageChain>()
                            imagesUrl.forEach {
                                val imageByteArray = it.toURL().readBytes()
                                val imageFileType = imageByteArray.getFileType()
                                val imageBedUrl = ImageBed.upload(imageByteArray, imageFileType)
                                val msg = MessageChain.Builder()
                                    .addText(imageBedUrl.makeShortLink())
                                    .addNewLine(2)
                                    .addText(imageBedUrl)
                                    .build()
                                messages.add(msg)
                            }
                            message.reply(messages.asNode(configManager.selfId))
                        }
                    } catch (e: Exception) {
                        message.reply("上传失败: ${e.message}")
                    }
                }
            }
        }

        val matchedResult = githubRegex.find(message.rawMessage)
        if (message.rawMessage.contains("github.com") && matchedResult != null) {
            val (user, repo) = matchedResult.destructured
            GitHubParseCommand.parse(message, user, repo)
        }

        coroutineScope.launch {
            message.message.forEach {
                if (it.type == ArrayMessageType.image) {
                    val filename = it.data.file!!.split("=").last() + ".png"
                    URI(it.data.file!!).toURL().openConnection().inputStream.use { input ->
                        File("$ROOT_PATH/caches/images/$filename").outputStream().use { output ->
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
        event.saveTo("$ROOT_PATH/caches/")
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

    override suspend fun onGroupPoke(event: PokeEvent) {
        if (event.targetId == configManager.selfId) {
            val msg = MessageChain.Builder()
                .addAt(event.userId)
                .addText("你${event.action.first()}牛魔呢")
                .build()
            this.sendGroupMessage(event.groupId!!, msg)
        }
    }

    override suspend fun onBeKicked(groupId: Long, operator: Long, time: Long) {
        blackListManager.insertGroup(groupId, operator, time)
        this.sendPrivateMessage(
            configManager.noticeUser,
            "被: ${groupId}踢出! 操作人: $operator 时间: ${time.convertToDate()} 已将其拉入黑名单!"
        )
    }
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
    initCommandAndItem(rob)
    initSetuIndex()
}