/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.misc.PastebinCommand
import cn.rtast.fancybot.commands.misc.ReactionCommand
import cn.rtast.fancybot.commands.misc.ScanQRCodeCommand
import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.commands.parse.*
import cn.rtast.fancybot.commands.reply.ImageBedCommand
import cn.rtast.fancybot.enums.WSType
import cn.rtast.fancybot.util.*
import cn.rtast.fancybot.util.misc.convertToDate
import cn.rtast.fancybot.util.misc.initBackgroundTasks
import cn.rtast.fancybot.util.misc.initCommand
import cn.rtast.fancybot.util.misc.initFilesDir
import cn.rtast.fancybot.util.misc.initItems
import cn.rtast.fancybot.util.misc.initSetuIndex
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.*
import cn.rtast.rob.entity.custom.BeKickEvent
import cn.rtast.rob.entity.custom.MemberLeaveEvent
import cn.rtast.rob.entity.lagrange.FileEvent
import cn.rtast.rob.entity.lagrange.PokeEvent
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class FancyBot : OneBotListener {

    private val logger = Logger.getLogger<FancyBot>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onWebsocketOpenEvent() {
        instance.action.sendPrivateMessage(configManager.noticeUser, "FancyBot启动完成~")
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

        if (message.text.contains("原神")) {
            (0..10).forEach { _ -> message.reaction(QQFace.entries.random()) }
            message.reply("你原神牛魔呢")
            delay(1500L)
            message.reply("但是话又说回来.....启动！！")
        }

        GitHubParseCommand.parse(message)
        ReverseGIFCommand.callback(message)
        AsciiArtCommand.callback(message)
        ScanQRCodeCommand.callback(message)
        BiliVideoParseCommand.parse(this, message)
        BiliUserParseCommand.parse(this, message)
        DouyinVideoParseCommand.parse(message)
        YoutubeVideoParseCommand.parse(message)

        if (message.text.toList().any { it in arrayListOf('*', '-', '/', '+', '=') }) {
            val calculateResult = CalculateCommand.parse(message.rawMessage)
            calculateResult?.let { message.reply(calculateResult) }
        }

        if (message.message.any { it.type == ArrayMessageType.reply }) {
            val command = message.message.reversed().find { it.type == ArrayMessageType.text }!!.data.text!!
            val replyId = message.message.find { it.type == ArrayMessageType.reply }!!.data.id!!
            val getMsg = message.action.getMessage(replyId.toString().toLong())
            val plainTextContent = getMsg.message
                .filter { it.type == ArrayMessageType.text }
                .joinToString { it.data.text!! }
            if (command.contains("倒放") || command.contains("/df")) {
                // 使用回复消息的方式对一个gif倒放
                ReverseGIFCommand.reverse(message, getMsg)
            }
            if (command.contains("/sl") || command.contains("/short")) {
                // 使用回复消息的方式快速对一个url消息创建一个短链接
                message.reply(plainTextContent.trim().makeShortLink())
            }
            if (command.contains("/pb") || command.contains("/pastebin")) {
                // 使用回复消息的方式快速将创建一个pastebin
                val pastebinUrl = PastebinCommand.createPastebin(plainTextContent)
                message.reply(pastebinUrl)
            }
            if (command.contains("/ascii") || command.contains("/asc")) {
                // 使用回复消息的方式直接对一个图片进行生成Ascii art的操作
                val url = AsciiArtCommand.getImageUrl(getMsg)
                val image = AsciiArtCommand.generateAsciiArt(url)
                message.reply(image)
            }
            if (command.contains("图来") || command.contains("图链")) {
                // 获取一个或多个图片的链接, 并且生成一个或多个短链接
                ImageURLCommand.callback(message, getMsg)
            }
            if (command.contains("reaction")) {
                // 使用reaction刷屏回应一条消息
                ReactionCommand.reaction(message)
            }
            if (command.contains("图床")) {
                // 将一个图片上传到图床
                ImageBedCommand.execute(getMsg, message)
            }
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
        instance.action.sendGroupMessage(message.groupId, msg)
    }

    override suspend fun onWebsocketErrorEvent(ex: Exception) {
        ex.printStackTrace()
    }

    override suspend fun onAddFriendRequest(event: AddFriendRequestEvent) {
        event.approve()
    }

    override suspend fun onGroupFileUpload(event: FileEvent) {
        event.saveTo("$ROOT_PATH/caches/")
    }

    override suspend fun onLeaveEvent(event: MemberLeaveEvent) {
        val msg = MessageChain.Builder()
            .addText("有人坐飞船离开了本星系~")
            .addNewLine()
            .addText("QQ: ${event.userId} | 操作者: ${if (event.operator == 0L) "主动退出" else event.operator}")
            .addNewLine()
            .addText("下次再见吧~~~")
            .build()
        event.action.sendGroupMessage(event.groupId, msg)
    }

    override suspend fun onGroupPoke(event: PokeEvent) {
        if (event.targetId == configManager.selfId) {
            val msg = MessageChain.Builder()
                .addAt(event.userId)
                .addText("你${event.action.first()}牛魔呢")
                .build()
            instance.action.sendGroupMessage(event.groupId!!, msg)
        }
    }

    override suspend fun onBeKicked(event: BeKickEvent) {
        blackListManager.insertGroup(event.groupId, event.operator, event.time)
        instance.action.sendPrivateMessage(
            configManager.noticeUser,
            "被: ${event.groupId}踢出! 操作人: ${event.operator} 时间: ${event.time.convertToDate()} 已将其拉入黑名单!"
        )
    }
}

val fancyBot = FancyBot()
val workType = configManager.wsType
val accessToken = configManager.accessToken
val instance = if (workType == WSType.Client) {
    val address = configManager.wsAddress
    ROneBotFactory.createClient(address, accessToken, fancyBot)
        .also { it.addListeningGroups(*configManager.listeningGroups.toLongArray()) }
} else {
    val port = configManager.wsPort
    ROneBotFactory.createServer(port, accessToken, fancyBot)
        .also { it.addListeningGroups(*configManager.listeningGroups.toLongArray()) }
}

suspend fun main() {
    initDatabase()
    initFilesDir()
    initCommand()
    initSetuIndex()
    initItems()
    initBackgroundTasks(instance)
}