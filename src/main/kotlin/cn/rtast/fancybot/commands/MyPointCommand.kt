/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.ADMINS
import cn.rtast.fancybot.signManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.UserRole
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class MyPointCommand : BaseCommand() {
    override val commandNames = listOf("/我的点数", "/mp")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        // query target's point
        if (args.isNotEmpty() && (message.sender.role == UserRole.admin || message.sender.userId in ADMINS)) {
            val targetId = args.first().toLong()
            val targetData = signManager.getStatus(targetId)
            val msg = MessageChain.Builder().addAt(message.sender.userId)

            if (targetData == null) {
                msg.addText("用户不存在, 需要先签到一次才能查询点数呢~")
            } else {
                msg.addText("用户: ${targetData.id} 的点数有: ${targetData.points}")
            }
            listener.sendGroupMessage(message.groupId, msg.build())
            return
        }
        // query self point
        val status = signManager.getStatus(message.sender.userId)
        if (status == null) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你还没有签到过呢! 先发送‘/签到’再来查询吧~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        } else {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("你当前的点数为: ${status.points}")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
        }
    }
}
