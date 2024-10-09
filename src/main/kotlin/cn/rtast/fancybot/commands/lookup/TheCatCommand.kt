/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.entity.thecat.Cat
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class TheCatCommand : BaseCommand() {
    override val commandNames = listOf("随机猫咪", "随机猫猫")

    private val theCatApiUrl = "https://api.thecatapi.com/v1/images/search"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val imageBase64= Http.get(theCatApiUrl)
            .fromArrayJson<List<Cat>>().first().url
            .proxy.toURL().readBytes().encodeToBase64()
        val msg = MessageChain.Builder().addImage(imageBase64, true).build()
        message.reply(msg)
    }
}
