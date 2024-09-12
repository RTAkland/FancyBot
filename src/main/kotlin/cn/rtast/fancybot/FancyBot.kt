/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.*
import cn.rtast.fancybot.commands.misc.BVParseCommand
import cn.rtast.fancybot.commands.misc.ImageURLCommand
import cn.rtast.fancybot.commands.misc.ReverseGIFCommand
import cn.rtast.fancybot.entity.enums.WSType
import cn.rtast.fancybot.items.BaisiItem
import cn.rtast.fancybot.items.HeisiItem
import cn.rtast.fancybot.items.SetuItem
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.fancybot.util.file.NiuziManager
import cn.rtast.fancybot.util.file.SignManager
import cn.rtast.fancybot.util.initDatabase
import cn.rtast.fancybot.util.item.ItemManager
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.GroupRevokeMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import okhttp3.OkHttpClient
import okhttp3.Request

class FancyBot : OBMessage {

    private val tempOkHttpClient = OkHttpClient()

    override suspend fun onGroupMessage(message: GroupMessage, json: String) {
        val sender = message.sender.nickname
        val senderId = message.sender.userId
        val msg = message.rawMessage
        val groupId = message.groupId
        println("$sender($senderId: $groupId): $msg")
        if (message.rawMessage.startsWith("BV") ||
            message.rawMessage.startsWith("https://www.bilibili.com") ||
            message.rawMessage.contains("https://b23.tv/")
        ) {
            // parse bilibili video with a link, bvid or b23.tv link
            val bvid = if (message.rawMessage.startsWith("BV")) {
                message.rawMessage
            } else if (message.rawMessage.contains("https://b23.tv/")) {
                val shortUrl = message.rawMessage.split(" ").last()
                val request = Request.Builder().url(shortUrl).build()
                val redirectedUrl = tempOkHttpClient.newCall(request).execute()
                redirectedUrl.use {
                    it.request.url.toString().split("/")[4].split("?").first()
                }
            } else {
                message.rawMessage.split("/")[4]
            }
            BVParseCommand.parse(this, bvid, message)
        }
        if (message.message.any { it.type == ArrayMessageType.reply }) {  // Image url parse
            val command = message.message.reversed().find { it.type == ArrayMessageType.text }!!.data.text!!
            val replyId = message.message.find { it.type == ArrayMessageType.reply }!!.data.id!!
            if (command.contains("图来") || command.contains("图链")) {
                // get image url
                this.getMessage(replyId.toString().toLong(), "imageUrl", message.groupId)
            }
            if (command.contains("倒放") || command.contains("df")) {
                // reverse gif
                this.getMessage(replyId.toString().toLong(), "reverseGif", message.groupId)
            }
        }
    }

    override suspend fun onGroupMessageRevoke(message: GroupRevokeMessage) {
        val msg = MessageChain.Builder()
            .addText("用户: ${message.userId} 被: ${message.operatorId} 撤回了一条消息")
            .addNewLine()
            .addText("使用/revoke ${message.messageId} 来获取被撤回的消息")
            .build()
        this.sendGroupMessage(message.groupId, msg)
    }


    override suspend fun onGetGroupMessageResponse(message: GetMessage) {
        when (message.data.id) {
            "revoke" -> AntiRevokeCommand.callback(this, message)
            "imageUrl" -> ImageURLCommand.callback(this, message)
            "reverseGif" -> ReverseGIFCommand.callback(this, message)
        }
    }

    override suspend fun onWebsocketErrorEvent(ex: Exception) {
        ex.printStackTrace()
    }
}

val configManager = ConfigManager()
val itemManager = ItemManager()
val signManager = SignManager()
val niuziManager = NiuziManager()

val items = listOf(
    HeisiItem(),
    BaisiItem(),
    SetuItem()
)

val commands = listOf(
    EchoCommand(), JrrpCommand(),
    MusicCommand(), SignCommand(),
    RedeemCommand(), MyPointCommand(),
    HitokotoCommand(), FKXQSCommand(),
    QRCodeCommand(), AntiRevokeCommand(),
    MCPingCommand(), HelpCommand(),
    WeatherCommand(), CigaretteCommand(),
    RemakeCommand(), PixivCommand(),
    RUACommand(), NslookupCommand(),
    NiuziSignCommand(), MyNiuziCommand(),
    JiJianCommand(), LikeMeCommand()
)

suspend fun main() {
    val fancyBot = FancyBot()
    val workType = configManager.wsType
    val accessToken = configManager.accessToken
    val rob = if (workType == WSType.Client) {
        val address = configManager.wsAddress
        ROneBotFactory.createClient(address, accessToken, fancyBot)
    } else {
        val port = configManager.wsPort
        ROneBotFactory.createServer(port, accessToken, fancyBot)
    }
    initDatabase()
    val commandManager = rob.commandManager
    commands.forEach { commandManager.register(it) }
    items.forEach { itemManager.register(it) }
    rob.addListeningGroups(*configManager.listeningGroups.toLongArray())
}