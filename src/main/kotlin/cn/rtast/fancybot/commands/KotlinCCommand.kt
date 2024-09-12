/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.kcs.KCSPayload
import cn.rtast.fancybot.entity.kcs.KCSResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class KotlinCCommand : BaseCommand() {
    override val commandNames = listOf("/kotlinc", "/ktc")

    private val kotlinCompilerServer = "https://api.kotlinlang.org/api/2.0.20/compiler/run"

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("使用`/ktc` <code>传入Kotlin代码即可执行~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val code = args.joinToString(" ")
        val response = Http.post<KCSResponse>(
            kotlinCompilerServer, jsonBody = KCSPayload(listOf(KCSPayload.File(code))).toJson()
        )
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("执行结果如下:")
            .addNewLine()
            .addText(response.text.replace("</outStream>", "").replace("<outStream>", ""))
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}