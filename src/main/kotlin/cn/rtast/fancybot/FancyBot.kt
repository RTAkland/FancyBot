/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/26
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.*
import cn.rtast.fancybot.commands.CompilerCommand
import cn.rtast.fancybot.commands.lookup.CigaretteCommand
import cn.rtast.fancybot.commands.lookup.DomainPriceCommand
import cn.rtast.fancybot.commands.lookup.GPTCommand
import cn.rtast.fancybot.commands.lookup.GithubUserCommand
import cn.rtast.fancybot.commands.lookup.MCPingCommand
import cn.rtast.fancybot.commands.lookup.MusicCommand
import cn.rtast.fancybot.commands.lookup.MusicPlayUrlCommand
import cn.rtast.fancybot.commands.lookup.NslookupCommand
import cn.rtast.fancybot.commands.lookup.PixivCommand
import cn.rtast.fancybot.commands.lookup.QRCodeCommand
import cn.rtast.fancybot.commands.lookup.WeatherCommand
import cn.rtast.fancybot.commands.parse.BVParseCommand
import cn.rtast.fancybot.commands.parse.GitHubParseCommand
import cn.rtast.fancybot.commands.parse.ImageURLCommand
import cn.rtast.fancybot.commands.parse.ReverseGIFCommand
import cn.rtast.fancybot.commands.parse.filterBadWord
import cn.rtast.fancybot.commands.record.JiJianCommand
import cn.rtast.fancybot.commands.record.JrrpCommand
import cn.rtast.fancybot.commands.record.MyNiuziCommand
import cn.rtast.fancybot.commands.record.MyPointCommand
import cn.rtast.fancybot.commands.record.NiuziSignCommand
import cn.rtast.fancybot.commands.record.RedeemCommand
import cn.rtast.fancybot.commands.record.SignCommand
import cn.rtast.fancybot.entity.bili.CardShare
import cn.rtast.fancybot.entity.enums.WSType
import cn.rtast.fancybot.items.BaisiItem
import cn.rtast.fancybot.items.HeisiItem
import cn.rtast.fancybot.items.SetuItem
import cn.rtast.fancybot.util.file.BadWordManager
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.fancybot.util.file.NiuziManager
import cn.rtast.fancybot.util.file.SignManager
import cn.rtast.fancybot.util.initDatabase
import cn.rtast.fancybot.util.item.ItemManager
import cn.rtast.fancybot.util.str.fromJson
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.AddFriendRequest
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.GroupRevokeMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class FancyBot : OneBotListener {

    override suspend fun onGroupMessage(message: GroupMessage, json: String) {
        val sender = message.sender.nickname
        val senderId = message.sender.userId
        val msg = message.rawMessage
        val groupId = message.groupId
        println("$sender($senderId: $groupId): $msg")

        filterBadWord(message)

        if (message.rawMessage.startsWith("https://github.com/") || message.rawMessage.startsWith("git@github.com:")) {
            GitHubParseCommand.parse(this, message)
        }

        if (message.rawMessage.startsWith("BV") ||
            message.rawMessage.startsWith("https://www.bilibili.com") ||
            message.rawMessage.contains("https://b23.tv/") ||
            message.message.find { it.type == ArrayMessageType.json } != null
        ) {
            // parse bilibili video with a link, bvid or b23.tv link
            val bvid = if (message.rawMessage.startsWith("BV")) {
                message.rawMessage
            } else if (message.rawMessage.split(" ").last().startsWith("https://b23.tv/")) {
                val shortUrl = message.rawMessage.split(" ").last()
                BVParseCommand.getShortUrlBVID(shortUrl)
            } else if (message.message.find { it.type == ArrayMessageType.json } != null) {
                val card = message.message.find { it.type == ArrayMessageType.json }!!
                    .data.data!!.toString().fromJson<CardShare>()
                val shortUrl = if (card.meta.detail == null) {
                    if (!card.meta.news?.tag!!.contains("哔哩哔哩")) return
                    card.meta.news.jumpUrl
                } else {
                    if (!card.meta.detail.title.contains("哔哩哔哩")) return
                    card.meta.detail.qqDocUrl
                }
                BVParseCommand.getShortUrlBVID(shortUrl)
            } else {
                message.rawMessage.split("?").first().split("/").filter { it.isNotEmpty() && it.isNotBlank() }.last()
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
        if (!configManager.enableAntiRevoke) return
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

    override suspend fun onAddFriendRequest(event: AddFriendRequest) {
        event.approve()
    }
}

val configManager = ConfigManager()
val itemManager = ItemManager()
val signManager = SignManager()
val niuziManager = NiuziManager()
val badWordManager = BadWordManager()

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
    JiJianCommand(), LikeMeCommand(),
    CompilerCommand(), GPTCommand(),
    GithubUserCommand(), AboutCommand(),
    MusicPlayUrlCommand(), DomainPriceCommand(),
    SendMailCommand(), ZiBiCommand(),
    UnsetZiBiCommand()
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