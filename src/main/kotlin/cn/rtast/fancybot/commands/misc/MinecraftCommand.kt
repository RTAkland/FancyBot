/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/9
 */

@file:Suppress("KDocUnresolvedReference", "unused")

package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands.lookup.NslookupCommand
import cn.rtast.fancybot.commands.lookup.WikipediaCommand.Companion.extractPlainTextFromHtml
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.db.deleteAccessTokenById
import cn.rtast.fancybot.db.getAccessTokenById
import cn.rtast.fancybot.db.insertNewAccessToken
import cn.rtast.fancybot.entity.mc.DecodedSkin
import cn.rtast.fancybot.entity.mc.MCVersion
import cn.rtast.fancybot.entity.mc.MCWikiPageResponse
import cn.rtast.fancybot.entity.mc.MCWikiSearchResponse
import cn.rtast.fancybot.entity.mc.Skin
import cn.rtast.fancybot.entity.mc.Username
import cn.rtast.fancybot.entity.mc.oauth.DeviceCodeResponse
import cn.rtast.fancybot.entity.mc.oauth.LoginXboxLivePayload
import cn.rtast.fancybot.entity.mc.oauth.LoginXboxLiveResponse
import cn.rtast.fancybot.entity.mc.oauth.ObtainXSTSPayload
import cn.rtast.fancybot.entity.mc.oauth.ObtainXSTSResponse
import cn.rtast.fancybot.entity.mc.oauth.RedeemCodeResponse
import cn.rtast.fancybot.enums.mc.MCVersionType
import cn.rtast.fancybot.rconManager
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Logger
import cn.rtast.fancybot.util.mcbot.MCClient
import cn.rtast.fancybot.util.misc.isFullyTransparent
import cn.rtast.fancybot.util.misc.scaleImage
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.convertToHTML
import cn.rtast.fancybot.util.str.decodeToString
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromJson
import cn.rtast.fancybot.util.str.generateRandomString
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.fancybot.util.str.uriEncode
import cn.rtast.motdpinger.BedrockPing
import cn.rtast.motdpinger.JavaPing
import cn.rtast.rcon.exceptions.AuthFailedException
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.PrivateMessage
import cn.rtast.rob.segment.NewLine
import cn.rtast.rob.segment.Text
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.toMessageChain
import cn.rtast.rob.util.ob.toNode
import okhttp3.FormBody
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.SocketException
import java.net.URI
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.text.split

@CommandDescription("获取MC最新的版本!")
class MCVersionCommand : BaseCommand() {
    override val commandNames = listOf("mc版本")

    private val javaEditionAPI = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val mcVersion = Http.get<MCVersion>(javaEditionAPI)
        val msg = MessageChain.Builder()
            .addText("最新正式版: ${mcVersion.latest.release}")
            .addNewLine()
            .addText("发布时间: ${mcVersion.versions.find { it.type == MCVersionType.release }?.releaseTime}")
            .addNewLine()
            .addText("最新测试版: ${mcVersion.latest.snapshot}")
            .addNewLine()
            .addText("测试版发布时间: ${mcVersion.versions.find { it.type == MCVersionType.snapshot }?.releaseTime}")
            .build()
        message.reply(msg)
    }
}

@CommandDescription("查询来自minecraft.wiki中的wiki页面")
class MinecraftWikiCommand : BaseCommand() {
    override val commandNames = listOf("/mcwiki")

    private val baseApiUrl = "https://zh.minecraft.wiki/rest.php/v1"
    private val logger = Logger.getLogger<MinecraftWikiCommand>()

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val action = args.first()
        when (action) {
            "s", "搜索" -> {
                val searchKeyword = args.drop(1).joinToString(" ").trim()
                val messages = mutableListOf<MessageChain>()
                val headerMessage = listOf("结果如下:").toMessageChain()
                messages.add(headerMessage)
                Http.get<MCWikiSearchResponse>(
                    "$baseApiUrl/search/title",
                    mapOf("q" to searchKeyword, "limit" to 10)
                ).pages.forEach {
                    val msg = MessageChain.Builder()
                    val imageUrl = it.thumbnail
                    if (imageUrl != null) {
                        val imageBase64 = imageUrl.url.toURL().readBytes().encodeToBase64()
                        msg.addImage(imageBase64, true)
                    }
                    msg.addText("标题: ${it.title}")
                        .addNewLine()
                        .addText("ID: ${it.id}")
                        .addNewLine()
                        .addText("https://zh.minecraft.wiki/w/${it.title.uriEncode}")
                    messages.add(msg.build())
                }
                message.reply(messages.toNode(configManager.selfId))
            }

            else -> {
                val title = args.joinToString(" ").trim()
                var response = Http.get<MCWikiPageResponse>("$baseApiUrl/page/$title")
                if (response.httpCode != null) {
                    logger.info("页面($title)不存在")
                    message.reply("页面($title)不存在")
                    return
                }
                if (response.redirectTarget != null) {
                    logger.info("重定向Wiki页面到: ${response.redirectTarget}")
                    response = Http.get<MCWikiPageResponse>(response.redirectTarget)
                }
                val messages = mutableListOf<MessageChain>()
                val headerMsg = listOf("标题${response.title}的内容如下").toMessageChain()
                val footerMsg = listOf(
                    Text("数据来源: https://zh.minecraft.wiki"),
                    NewLine(),
                    Text("协议: ${response.license.title}(${response.license.url})")
                ).toMessageChain()
                val bodyMsg = MessageChain.Builder()
                    .addText(response.source.convertToHTML().extractPlainTextFromHtml())
                    .build()
                messages.add(headerMsg)
                messages.add(bodyMsg)
                messages.add(footerMsg)
                message.reply(messages.toNode(configManager.selfId))
            }
        }
    }
}

@CommandDescription("帮助你寻找指定空置域大小并且在某个区块范围内的最多史莱姆区块范围")
class SlimeChunkHelperCommand : BaseCommand() {
    override val commandNames = listOf("/slime")

    private fun isSlimeChunk(worldSeed: Long, chunkX: Int, chunkZ: Int): Boolean {
        val rng = Random(
            worldSeed + (chunkX * chunkX) * 0x4C1906 +
                    (chunkX * 0x5AC0DB) + (chunkZ * chunkZ) * 0x4307A7L +
                    (chunkZ * 0x5F24F) xor 0x3AD8025FL
        )
        return rng.nextInt(10) == 0
    }

    private fun countSlimeChunksInRegion(xStart: Int, zStart: Int, chunkSize: Int, worldSeed: Long): Int {
        var count = 0
        for (x in xStart until xStart + chunkSize) {
            for (z in zStart until zStart + chunkSize) {
                if (isSlimeChunk(worldSeed, x, z)) {
                    count++
                }
            }
        }
        return count
    }

    private fun findMaxSlimeChunks(
        worldSeed: Long,
        searchRange: Int,
        chunkSize: Int,
    ): Pair<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int> {
        var maxCount = 0
        var bestStartLocation = Pair(0, 0)
        var bestEndLocation = Pair(0, 0)
        for (x in -searchRange until searchRange - chunkSize + 1) {
            for (z in -searchRange until searchRange - chunkSize + 1) {
                val count = countSlimeChunksInRegion(x, z, chunkSize, worldSeed)
                if (count > maxCount) {
                    maxCount = count
                    bestStartLocation = Pair(x, z)
                    bestEndLocation = Pair(x + chunkSize - 1, z + chunkSize - 1)
                }
            }
        }
        return Pair(Pair(bestStartLocation, bestEndLocation), maxCount)
    }

    private fun generateSlimeChunkImage(startX: Int, startZ: Int, endX: Int, endZ: Int, worldSeed: Long): String {
        val width = endX - startX + 1
        val height = endZ - startZ + 1
        val pixelSize = 15
        val imageWidth = width * pixelSize
        val imageHeight = height * pixelSize
        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics
        val borderColor = Color.LIGHT_GRAY
        for (x in startX..endX) {
            for (z in startZ..endZ) {
                val isSlime = isSlimeChunk(worldSeed, x, z)
                val fillColor = if (isSlime) Color.PINK else Color.WHITE
                val px = (x - startX) * pixelSize
                val pz = (z - startZ) * pixelSize
                graphics.color = fillColor
                graphics.fillRect(px, pz, pixelSize, pixelSize)
                graphics.color = borderColor
                graphics.drawRect(px, pz, pixelSize, pixelSize)
            }
        }
        graphics.dispose()
        return image.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("此指令可以借助一个种子来帮助查找最适合史莱姆农场的位置")
                .addNewLine()
                .addText("如果不指定史莱姆农场的宽度的话默认为17x17区块大小")
                .addNewLine()
                .addText("不指定搜索区块范围默认为X: 500~-500, Y: 500~-500, 共计搜索100万个区块")
                .addNewLine()
                .addText("还可以指定空置域的大小, 不指定默认为17x17")
                .addNewLine()
                .addText("你可以使用`/slime <种子> [范围(例如600)] [空置域大小]`")
                .build()
            message.reply(msg)
            return
        }
        val seed = args.first().toLong()
        val slimeFarmSize = if (args.size == 1) 17 else args[1].toInt()
        val range = if (args.size == 1) 500 else args[2].toInt()
        val (location, slimeCount) = findMaxSlimeChunks(seed, range, slimeFarmSize)
        val startX = location.first.first
        val startZ = location.first.second
        val endX = location.second.first
        val endZ = location.second.second
        val image = generateSlimeChunkImage(startX, startZ, endX, endZ, seed)
        val msg = MessageChain.Builder()
            .addText("起始区块坐标X:$startX Y:$startZ")
            .addNewLine()
            .addText("结束区块坐标X:$endX Y:$endZ")
            .addNewLine()
            .addText("共计史莱姆区块数量: $slimeCount")
            .addImage(image, true)
            .build()
        message.reply(msg)
    }
}


@CommandDescription("通过游戏名称或UUID来获取他的皮肤")
class MCSkinCommand : BaseCommand() {
    override val commandNames = listOf("/skin")

    private val uuidQueryUrl = "https://api.mojang.com/users/profiles/minecraft"
    private val skinUrl = "https://sessionserver.mojang.com/session/minecraft/profile"

    private fun String.isUsername(): Boolean {
        val usernamePattern = Regex("^[a-zA-Z][a-zA-Z0-9_]{2,15}$")
        return usernamePattern.matches(this)
    }

    /**
     * 截取整张皮肤中的头部部分, 如果是单层皮肤则直接截取脸部
     * 如果是双层皮肤也就将脸部作为背景图层, 将头发绘制在脸部上
     * 代码来自
     * `[YeeeesMOTD]
     * (https://github.com/RTAkland/YeeeesMOTD/blob/main/core/src/main/kotlin/cn/rtast/yeeeesmotd/utils/SkinHeadUtil.kt)`
     */
    private fun getSkinHead(skinUrl: String): String {
        val url = URI(skinUrl).toURL()
        val image = ImageIO.read(url)
        var subImage = image.getSubimage(8, 8, 8, 8)

        if (subImage.isFullyTransparent()) {
            subImage = image.getSubimage(40, 8, 8, 8)
        } else {
            val hairLayer = image.getSubimage(40, 8, 8, 8)
            val combined = BufferedImage(subImage.width, subImage.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = combined.createGraphics()
            g2d.drawImage(subImage, 0, 0, null)
            g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
            g2d.drawImage(hairLayer, 0, 0, null)
            g2d.dispose()
            subImage = combined
        }
        val zoom = subImage.scaleImage(128 to 128)
        return zoom.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/skin <uuid/游戏名称>`即可查询他的皮肤~ (仅限Java版)")
            return
        }
        try {
            val nameOrUUID = args.first().trim()
            val uuid = if (nameOrUUID.isUsername()) Http.get<Username>("$uuidQueryUrl/$nameOrUUID").id
            else nameOrUUID
            val skinUrl = Http.get<Skin>("$skinUrl/$uuid")
                .properties.first().value.decodeToString().fromJson<DecodedSkin>()
                .textures.skin.url
            val headBase64 = this.getSkinHead(skinUrl)
            val msg = MessageChain.Builder()
                .addImage(headBase64, true)
                .addText(skinUrl)
                .build()
            message.reply(msg)
        } catch (_: Exception) {
            message.reply("输入错误, 检查一下是否输入正确吧~")
        }
    }
}


@CommandDescription("PingMC服务器")
class MCPingCommand : BaseCommand() {
    override val commandNames = listOf("/mcping")

    private fun String.removeColorCodes(): String {
        return this.replace(Regex("§."), "")
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("使用方法: /mcping <host>:[port] [java|be]")
                .addNewLine()
                .addText("不添加平台默认Java版~")
                .build()
            message.reply(msg)
            return
        }
        val platform = if (args.size == 2) args.last() else "java"
        when (platform) {
            "java", "je", "Java", "JAVA" -> {
                val parts = args.first().split(":")
                var port = if (parts.size == 1) 25565 else parts.last().toInt()
                var host = parts.first()
                val srv = NslookupCommand.srv(host)
                if (srv.isNotEmpty()) {
                    val srv = srv.first().split(" ")
                    port = srv[2].toInt()
                    host = srv.last()
                }
                val response = JavaPing().ping(host, port, 10000)
                if (response == null) {
                    message.reply("无法获取服务器的消息请检查输入是否正确 >>> $host:$port")
                    return
                }
                val msg = MessageChain.Builder()
                response.favicon?.let { msg.addImage(response.favicon!!.replace("data:image/png;base64,", ""), true) }
                msg.addText("服务器地址: $host:$port")
                    .addNewLine()
                    .addText("服务器类型: Java | 延迟: ${response.latency}ms")
                    .addNewLine()
                    .addText("服务器版本: ${response.version.name}/${response.version.protocol}")
                    .addNewLine()
                    .addText("在线玩家数: ${response.players.online}/${response.players.max}")
                message.reply(msg.build())
            }

            "be", "bedrock", "BE", "Bedrock" -> {
                val parts = args.first().split(":")
                val port = if (parts.size == 1) 19132 else parts.last().toInt()
                val host = parts.first()
                try {
                    val response = BedrockPing().ping(host, port, 10000)
                    val msg = MessageChain.Builder()
                        .addText("服务器地址: $host:$port")
                        .addNewLine()
                        .addText("服务器类型: Bedrock(基岩版) | 延迟: ${response.latency}ms")
                        .addNewLine()
                        .addText("服务器版本: ${response.version}/${response.protocolVersion}")
                        .addNewLine()
                        .addText("在线玩家: ${response.onlinePlayers}/${response.maxPlayers}")
                        .addNewLine()
                        .addText("MOTD: ${response.motd.removeColorCodes()}")
                        .addNewLine()
                        .addText(response.subTitle.removeColorCodes())
                    message.reply(msg.build())
                } catch (_: SocketException) {
                    message.reply("无法从服务器接收Ping结果请检查服务器地址是否正确 >>> $host:$port")
                }
            }

            else -> {
                message.reply("输入错误请在 `java` 和 `be`中选择 >>> $platform")
            }
        }
    }
}

@CommandDescription("获取MC账号的AccessToken")
class MCLoginCommand : BaseCommand() {
    override val commandNames = listOf("/mclogin")

    private val deviceCodeUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode"
    private val redeemCodeUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token"
    private val xboxLiveAuthUrl = "https://user.auth.xboxlive.com/user/authenticate"
    private val obtainXSTSUrl = "https://xsts.auth.xboxlive.com/xsts/authorize"
    private val clientId = configManager.azureAppClientId
    private val waitingList = mutableListOf<Long>()
    private val deviceCodeMap = mutableMapOf<Long, String>()

    /**
     * 获取user code 和device code 和验证url
     */
    private fun getDeviceCode(): Triple<String, String, String> {
        val form = FormBody.Builder()
            .add("client_id", clientId)
            .add("scope", "XboxLive.signin")
            .build()
        val response = Http.post<DeviceCodeResponse>(deviceCodeUrl, form)
        return Triple(response.userCode, response.deviceCode, response.verificationUri)
    }

    /**
     * 将这个code兑换成一个access token
     */
    private fun redeemCode(deviceCode: String): String {
        val form = FormBody.Builder()
            .add("client_id", clientId)
            .add("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
            .add("device_code", deviceCode)
            .build()
        val response = Http.post<RedeemCodeResponse>(redeemCodeUrl, form)
        return response.accessToken
    }

    /**
     * 登录Xbox
     */
    private fun loginXboxLive(accessToken: String): Pair<String, String> {
        val payload = LoginXboxLivePayload(properties = LoginXboxLivePayload.Properties("d=$accessToken"))
        val response = Http.post<LoginXboxLiveResponse>(xboxLiveAuthUrl, payload.toJson())
        val token = response.token
        val uhs = response.displayClaims.xui.first().uhs
        return token to uhs
    }

    /**
     * 获取最后的mc access token, 这个access token可以直接用于登录游戏
     */
    private fun obtainXSTSToken(token: String, uhs: String): String {
        val payload = ObtainXSTSPayload(properties = ObtainXSTSPayload.Properties(listOf(token)))
        val response = Http.post<ObtainXSTSResponse>(obtainXSTSUrl, payload.toJson())
        return response.token
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("这个命令可以获取你的MC登录Token发送 `/mclogin login`进行操作吧")
            return
        }
        deleteAccessTokenById(message.sender.userId)
        val action = args.first()
        if (action == "确认" || action == "confirm") {
            if (message.sender.userId !in waitingList) {
                message.reply("你还没开始登陆呢")
            } else {
                waitingList.removeIf { it == message.sender.userId }
                val deviceCode = deviceCodeMap[message.sender.userId]!!
                deviceCodeMap.remove(message.sender.userId)
                val accessToken = this.redeemCode(deviceCode)
                val (token, uhs) = this.loginXboxLive(accessToken)
                val minecraftAccessToken = this.obtainXSTSToken(token, uhs)
                insertNewAccessToken(minecraftAccessToken, message.sender.userId)
                message.reply("你的账号token已经被保存到了数据库中, 请加机器人好友然后发送`/mclogin`来获取这个Token, Token会在你获取之后从数据库中删除")
            }
        } else {
            if (message.sender.userId !in waitingList) {
                waitingList.add(message.sender.userId)
                val deviceInfo = getDeviceCode()
                val msg = MessageChain.Builder()
                    .addText("你的用户代码是: ${deviceInfo.first}")
                    .addNewLine()
                    .addText("请前往: ${deviceInfo.third}进行验证")
                    .addNewLine()
                    .addText("登陆完成后发送`/mclogin confirm` 或 `/mclogin 确认`")
                    .build()
                deviceCodeMap[message.sender.userId] = deviceInfo.second
                message.reply(msg)
            } else {
                waitingList.removeIf { it == message.sender.userId }
                deviceCodeMap.remove(message.sender.userId)
                message.reply("回复错误已结束本次登录流程")
            }
        }
    }

    override suspend fun executePrivate(message: PrivateMessage, args: List<String>) {
        val token = getAccessTokenById(message.sender.userId)
        if (token == null) {
            message.reply("你还没登录呢!")
        } else {
            val msg = MessageChain.Builder()
                .addText("你的AccessToken如下: ")
                .addNewLine()
                .addText(token.token)
                .addNewLine()
                .addText("这个Token是敏感信息请妥善保管")
                .build()
            deleteAccessTokenById(message.sender.userId)
            message.reply(msg)
        }
    }
}

@CommandDescription("通过RCON来控制MC服务器(仅限私聊执行)")
class RCONCommand : BaseCommand() {
    override val commandNames = listOf("/rcon", "/r")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.reply("请在私聊中使用此命令")
    }

    override suspend fun executePrivate(message: PrivateMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("这条指令用于在群内操作mc服务器")
                .addNewLine()
                .addText("使用`/r add <name> <host> <port> <password>` 可以创建一条配置")
                .addNewLine()
                .addText("使用`/r <name> <command>`可以执行命令")
                .addNewLine()
                .addText("使用`/r remove <name>`可以移除这条RCON配置")
                .build()
            message.reply(msg)
            return
        }
        val action = args.first()
        when (action) {
            "get" -> {
                val allRcons = rconManager.getAllRconsById(message.sender.userId)
                if (allRcons.isEmpty()) {
                    message.reply("你还没有RCON配置呢, 发送`/r add <name> <host> <port> <password>`来创建一个配置吧")
                    return
                }
                val messages = mutableListOf<MessageChain>()
                    .also { it.add(listOf("所有配置如下").toMessageChain()) }
                allRcons.forEach {
                    val msg = MessageChain.Builder()
                        .addText("名称: ${it.name}")
                        .addNewLine()
                        .addText("地址: ${it.host}:${it.port}")
                        .addNewLine()
                        .addText("密码: *********")
                        .build()
                    messages.add(msg)
                }
                message.reply(messages.toNode(configManager.selfId))
            }

            "add" -> {
                val name = args[1]
                val host = args[2]
                val port = args[3].toInt()
                val password = args[4]
                rconManager.insertConfig(message.sender.userId, name, host, port, password)
                message.reply("成功增加一条记录: $name")
            }

            "remove" -> {
                val name = args.last()
                val count = rconManager.removeRRCON(message.sender.userId, name)
                if (count == 0) {
                    message.reply("RCON($name)不存在")
                } else {
                    message.reply("成功移除了$name")
                }
            }

            else -> {
                val name = args.first().trim()
                val command = args.drop(1).joinToString(" ").trim()
                val rconData = rconManager.getRcon(message.sender.userId, name)
                if (rconData == null) {
                    message.reply("RCON($name)配置不存在")
                } else {
                    try {
                        val result = rconManager.executeCommand(message.sender.userId, name, command)
                        val messages = mutableListOf<MessageChain>()
                            .also { it.add(listOf("执行结果如下").toMessageChain()) }
                            .also { it.add(listOf(result).toMessageChain()) }
                            .toNode(configManager.selfId)
                        message.reply(messages)
                    } catch (_: AuthFailedException) {
                        message.reply("执行失败, 密码不正确")
                    }
                }
            }
        }
    }
}

@CommandDescription("Java版服务器假人压测")
class MCBotCommand : BaseCommand() {
    override val commandNames = listOf("/mcbot")

    private val userClientMap = mutableMapOf<Long, MutableList<MCClient>>()

    private fun getPlayerList(host: String, port: Int): Pair<Int, Int> {
        val response = JavaPing().ping(host, port, 8000)!!
        return response.players.max to response.players.online
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val clients = mutableListOf<MCClient>()
        val action = args.first()
        when (action) {
            "start" -> {
                if (userClientMap.containsKey(message.sender.userId)) {
                    message.reply("你有一个正在进行的任务不能开启另外一个任务请先使用`/mcbot cancel`取消任务")
                    return
                }
                val address = args[1]
                val host = address.split(":").first()
                val port = address.split(":").last().toInt()
                val count = args[2].toInt()
                val executor = Executors.newFixedThreadPool(count)
                val (maxPlayer, onlinePlayer) = this.getPlayerList(host, port)
                val availableSit = maxPlayer - onlinePlayer
                if (count > availableSit) {
                    message.replyAsync("当前服务器仅剩余${availableSit}个玩家可进入!!")
                }
                (1..count).forEach {
                    val client = MCClient(host, port, generateRandomString())
                        .also { it.createClient() }
                    executor.execute { clients.add(client.runBot()) }
                }
                userClientMap[message.sender.userId] = clients
                message.reply("成功开启任务: $address, $count")
            }

            "cancel" -> {
                if (userClientMap.containsKey(message.sender.userId)) {
                    userClientMap.entries.forEach {
                        if (it.key == message.sender.userId) {
                            it.value.forEach { it.disconnect() }
                            userClientMap.remove(it.key)
                        }
                    }
                    message.reply("成功取消任务")
                } else {
                    message.reply("你还没开启任务呢")
                }
            }

            "message" -> {
                val content = args.drop(1).joinToString(" ").trim()
                userClientMap[message.sender.userId]!!.forEach { it.sendChat(content) }
            }
        }
    }
}