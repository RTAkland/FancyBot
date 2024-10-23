/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/23
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.HttpStatusCode
import cn.rtast.fancybot.util.misc.Resources
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("随机HTTP猫猫")
class HTTPCatCommand : BaseCommand() {
    override val commandNames = listOf("http猫猫", "HTTP猫猫")

    companion object {
        private const val HTTP_CAT_API = "https://http.cat/###.jpg"
        private val notFoundCatBase64 = Resources.loadFromResourcesAsBytes("httpcat/404.png")!!.encodeToBase64()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val image = try {
            val statusCode = if (args.isEmpty()) HttpStatusCode.entries.random().code.toString() else args.first()
            HTTP_CAT_API.replace("###", statusCode).toURL().readBytes().encodeToBase64()
        } catch (_: Exception) {
            notFoundCatBase64
        }
        val msg = MessageChain.Builder().addImage(image, true).build()
        message.reply(msg)
    }
}