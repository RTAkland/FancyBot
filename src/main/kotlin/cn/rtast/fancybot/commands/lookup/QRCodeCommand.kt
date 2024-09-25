/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("生成二维码")
class QRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/qr")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage("https://api.rtast.cn/api/generate_qr?data=$content")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}