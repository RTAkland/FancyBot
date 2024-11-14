/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.misc.JueCommand
import cn.rtast.fancybot.commands.misc.PastebinCommand
import cn.rtast.fancybot.commands.misc.ReactionCommand
import cn.rtast.fancybot.commands.misc.ScanQRCodeCommand
import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.commands.parse.*
import cn.rtast.fancybot.commands.reply.ImageBedCommand
import cn.rtast.fancybot.commands.reply.InvertImageCommand
import cn.rtast.fancybot.commands.reply.RandomRGBCommand
import cn.rtast.fancybot.commands.reply.SpeedUpGIFCommand
import cn.rtast.fancybot.enums.WSType
import cn.rtast.fancybot.util.CommandInterceptor
import cn.rtast.fancybot.util.Logger
import cn.rtast.fancybot.util.initDatabase
import cn.rtast.fancybot.util.misc.*
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.*
import cn.rtast.rob.entity.custom.BeKickEvent
import cn.rtast.rob.entity.custom.ErrorEvent
import cn.rtast.rob.entity.custom.MemberKickEvent
import cn.rtast.rob.entity.custom.MemberLeaveEvent
import cn.rtast.rob.entity.lagrange.FileEvent
import cn.rtast.rob.entity.lagrange.PokeEvent
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.enums.QQFace
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.onebot.OneBotAction
import cn.rtast.rob.onebot.OneBotListener
import cn.rtast.rob.segment.NewLine
import cn.rtast.rob.segment.Text
import com.madgag.gif.fmsware.GifDecoder
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

class FancyBot : OneBotListener {

    private val logger = Logger.getLogger<FancyBot>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onWebsocketOpenEvent(action: OneBotAction) {
        action.sendPrivateMessage(configManager.noticeUser, "FancyBot启动完成~")
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
        BiliVideoParseCommand.parse(message)
        BiliUserParseCommand.parse(message)
        DouyinVideoParseCommand.parse(message)
        YoutubeVideoParseCommand.parse(message)

        if (message.text.toList().any { it in arrayListOf('*', '-', '/', '+', '=') }) {
            val calculateResult = CalculateCommand.parse(message.rawMessage)
            calculateResult?.let { message.reply(calculateResult) }
        }

        if (message.message.any { it.type == ArrayMessageType.at }) {
            if (message.text.contains("/取头像")) {
                val target = message.message.find { it.type == ArrayMessageType.at }?.data?.qq!!
                val avatar = JueCommand.AVATAR_REPLACE_URL.replace("#{}", target)
                val msg = MessageChain.Builder()
                if (message.text.contains("黑白")) {
                    val greyscale = avatar.toURL()
                        .readBytes().toBufferedImage()
                        .toGrayscale().toByteArray()
                    val greyscaleShortLink = ImageBed.upload(greyscale).makeShortLink()
                    msg.addImage(greyscale.encodeToBase64(), true)
                        .addText(greyscaleShortLink)
                } else {
                    val shortLink = avatar.makeShortLink()
                    msg.addImage(avatar).addText(shortLink)
                }
                message.reply(msg.build())
            }
        }

        if (message.message.any { it.type == ArrayMessageType.reply }) {
            val command = message.text.trim()
            val replyId = message.message.find { it.type == ArrayMessageType.reply }!!.data.id!!
            val getMsg = message.action.getMessage(replyId.toString().toLong())
            val plainTextContent = getMsg.text
            when (command) {
                "/倒放", "/df" -> ReverseGIFCommand.reverse(message, getMsg)
                "/图来", "/图链" -> ImageURLCommand.callback(message, getMsg)
                "/图床" -> ImageBedCommand.execute(getMsg, message)
                "/reaction" -> ReactionCommand.reaction(message.action, message.groupId, getMsg.messageId)
                "/sl", "/short" -> message.reply(plainTextContent.trim().makeShortLink())
                "/rc", "/随机颜色" -> RandomRGBCommand.random(message, getMsg)
                "/颜色反转", "/iv" -> InvertImageCommand.invert(message, getMsg)
                "/ascii", "/asc" -> {
                    val url = AsciiArtCommand.getImageUrl(getMsg)
                    val image = AsciiArtCommand.generateAsciiArt(url)
                    message.reply(image)
                }

                "/pb", "/pastebin" -> {
                    val pastebinUrl = PastebinCommand.createPastebin(plainTextContent)
                    message.reply(pastebinUrl)
                }

                "/加速" -> {
                    val multiply = command.split("加速").last().toFloat()
                    SpeedUpGIFCommand.speedUp(message, getMsg, multiply)
                }

                "/黑白", "/灰度" -> {
                    val msg = MessageChain.Builder()
                    getMsg.message.filter { it.type == ArrayMessageType.image || it.type == ArrayMessageType.mface }
                        .forEach {
                            val imgUrl = it.data.file!!
                            val decoder = GifDecoder()
                            val imageStream = withContext(Dispatchers.IO) { imgUrl.toURL().openStream() }
                            decoder.read(imageStream)
                            if (decoder.frameCount == 0) {
                                msg.addImage(
                                    imageStream.readBytes().toBufferedImage().toByteArray().encodeToBase64(), true
                                )
                            } else {
                                val frames = (0 until decoder.frameCount).map { decoder.getFrame(it).toGrayscale() }
                                val base64 = decoder.makeGif(frames).encodeToBase64()
                                msg.addImage(base64, true)
                            }
                        }
                    message.reply(msg.build())
                }

                "/拆帧", "/cj" -> {
                    val gifStream = getMsg.images.first().file.toURL().openStream()
                    val decoder = GifDecoder()
                    decoder.read(gifStream)
                    val frames = (0 until decoder.frameCount).map { decoder.getFrame(it) }
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    ZipOutputStream(byteArrayOutputStream).use { zipOut ->
                        for ((index, image) in frames.withIndex()) {
                            val imageOutputStream = ByteArrayOutputStream()
                            ImageIO.write(image, "png", imageOutputStream)
                            val entry = ZipEntry("frame_$index.png")
                            zipOut.putNextEntry(entry)
                            zipOut.write(imageOutputStream.toByteArray())
                            zipOut.closeEntry()
                        }
                    }
                    val zipBytes = byteArrayOutputStream.toByteArray()
                    logger.info("Split frame size: ${zipBytes.size / 1024 / 1024}MB")
                    val imgBedUrl = ImageBed.upload(zipBytes, "zip")
                    val shortLink = imgBedUrl.makeShortLink()
                    val msg = Text(imgBedUrl) + NewLine() + Text(shortLink)
                    message.reply(msg)
                }
            }
        }

        coroutineScope.launch {
//            if (message.images.isNotEmpty()) {
//                message.images.forEach {
//                    val payload = NaiLongDetectPayload(it.file).toJson()
//                    val result = Http.post<NaiLongDetectResponse>(configManager.naiLongApiUrl, payload)
//                    logger.info("奶龙识别结果: ${result.result}")
//                    if (result.result) {
//                        message.reply("本群禁止发奶龙!")
//                    }
//                }
//            }
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
        message.action.sendGroupMessage(message.groupId, msg)
    }

    override suspend fun onWebsocketErrorEvent(event: ErrorEvent) {
        event.exception.printStackTrace()
    }

    override suspend fun onAddFriendRequest(event: AddFriendRequestEvent) {
        event.approve()
    }

    override suspend fun onGroupFileUpload(event: FileEvent) {
        event.saveTo("$ROOT_PATH/caches/")
    }

    override suspend fun onLeaveEvent(event: MemberLeaveEvent) {
        val nickname = event.action.getGroupMemberInfo(event.groupId, event.userId).nickname
        val msg = MessageChain.Builder()
            .addText("有人坐飞船离开了本星系~(主动退出)")
            .addNewLine()
            .addText("QQ: $nickname(${event.userId})")
            .addNewLine()
            .addText("下次再见吧~~~")
            .build()
        event.action.sendGroupMessage(event.groupId, msg)
    }

    override suspend fun onMemberKick(event: MemberKickEvent) {
        val nickname = event.action.getGroupMemberInfo(event.groupId, event.userId).nickname
        val msg = MessageChain.Builder()
            .addText("有人坐飞船离开了本星系~(管理员踢出)")
            .addNewLine()
            .addText("QQ: $nickname(${event.userId})")
            .addNewLine()
            .addText("下次再见吧~~~")
            .build()
        event.action.sendGroupMessage(event.groupId, msg)
    }

    override suspend fun onGroupPoke(event: PokeEvent) {
        if (event.targetId == configManager.selfId) {
            val msg = MessageChain.Builder()
                .addAt(event.userId)
                .addText("你${event.pokeAction.first()}牛魔呢")
                .build()
            event.action.sendGroupMessage(event.groupId!!, msg)
        }
    }

    override suspend fun onBeKicked(event: BeKickEvent) {
        blackListManager.insertGroup(event.groupId, event.operator, event.time)
        event.action.sendPrivateMessage(
            configManager.noticeUser,
            "被: ${event.groupId}踢出! 操作人: ${event.operator} 时间: ${event.time.convertToDate()} 已将其拉入黑名单!"
        )
    }
}

suspend fun main() {
    val fancyBot = FancyBot()
    val workType = configManager.wsType
    val accessToken = configManager.accessToken
    val instance = when (workType) {
        WSType.Client -> ROneBotFactory.createClient(configManager.wsAddress, accessToken, fancyBot)
        WSType.Server -> ROneBotFactory.createServer(configManager.wsPort, accessToken, fancyBot)
    }
    ROneBotFactory.interceptor = CommandInterceptor()
    instance.addListeningGroups(*configManager.listeningGroups.toLongArray())
    initDatabase()
    initFilesDir()
    initCommand()
    initSetuIndex()
    initItems()
    initBackgroundTasks(instance)
}