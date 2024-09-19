/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class QRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/qrcode", "/qr", "/二维码")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage("https://api.rtast.cn/api/generate_qr?data=$content")
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}