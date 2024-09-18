/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/18
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.badWordManager
import cn.rtast.rob.entity.GroupMessage

suspend fun filterBadWord(message: GroupMessage) {
    if (badWordManager.contain(message.rawMessage)) {
        message.reply("你不许说脏话")
        message.revoke()
    }
}
