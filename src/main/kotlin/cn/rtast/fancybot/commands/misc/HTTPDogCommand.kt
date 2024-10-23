/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/23
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.HttpStatusCode
import cn.rtast.fancybot.util.misc.Resources
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("随机HTTP狗狗")
class HTTPDogCommand : BaseCommand() {
    override val commandNames = listOf("http狗狗", "HTTP狗狗")

    private val notFoundCatBase64 = Resources.loadFromResourcesAsBytes("httpdog/999.jpg")!!.encodeToBase64()

    private fun loadHTTPDog(code: String): String {
        return Resources.loadFromResourcesAsBytes("httpdog/${code}.jpg")?.encodeToBase64() ?: notFoundCatBase64
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val statusCode = if (args.isEmpty()) HttpStatusCode.entries.random().code.toString() else args.first()
        val image = loadHTTPDog(statusCode)
        val msg = MessageChain.Builder().addImage(image, true).build()
        message.reply(msg)
    }
}