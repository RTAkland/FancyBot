/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands

import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

class QRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/qrcode", "/qr", "/二维码")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        listener.sendGroupMessage(
            message.groupId,
            "[CQ:at,qq=${message.sender.userId}][CQ:image,file=https://api.rtast.cn/api/generate_qr?data=$content]"
        )
    }
}