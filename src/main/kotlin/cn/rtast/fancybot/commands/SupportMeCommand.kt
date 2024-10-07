/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/7
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.ASSETS_BASE_URL
import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

@CommandDescription("给我打钱!")
class SupportMeCommand : BaseCommand() {
    override val commandNames = listOf("打钱", "给我打钱", "support", "sp")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val image = URI("$ASSETS_BASE_URL/images/048cc8af57f19850ca176f29e50b6215.png")
            .toURL().readBytes().encodeToBase64()
        val msg = MessageChain.Builder()
            .addImage(image, true)
            .build()
        message.reply(msg)
    }
}