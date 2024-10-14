/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/14
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.getMD5
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("查询用户的Gravatar头像")
class GravatarCommand : BaseCommand() {
    override val commandNames = listOf("/g", "/gravatar")

    companion object {
        private const val GRAVATAR_URL = "https://gravatar.rtast.cn/avatar"
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val emailMD5 = if (args.first().trim().lowercase().contains("@")) args.first().trim().lowercase().getMD5()
        else args.first().trim().lowercase()
        val avatarImage = "$GRAVATAR_URL/$emailMD5?size=256".toURL().readBytes().encodeToBase64()
        val msg = MessageChain.Builder().addImage(avatarImage, true).build()
        message.reply(msg)
    }
}