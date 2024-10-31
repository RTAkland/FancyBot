/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/31
 */


package cn.rtast.fancybot.commands.reply

import cn.rtast.fancybot.util.misc.invertColor
import cn.rtast.fancybot.util.misc.toBufferedImage
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.images
import cn.rtast.rob.util.ob.MessageChain

object InvertImageCommand {

    suspend fun invert(message: GroupMessage, getMsg: GetMessage.Message) {
        val msg = MessageChain.Builder()
        getMsg.images.forEach {
            val img = it.file.toURL().readBytes().toBufferedImage()
                .invertColor().toByteArray().encodeToBase64()
            msg.addImage(img, true)
        }
        message.reply(msg.build())
    }
}