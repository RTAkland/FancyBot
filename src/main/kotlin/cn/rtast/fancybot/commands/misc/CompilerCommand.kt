/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.compiler.GLOTPayload
import cn.rtast.fancybot.entity.compiler.GLOTResponse
import cn.rtast.fancybot.entity.compiler.KCSPayload
import cn.rtast.fancybot.entity.compiler.KCSResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class CompilerCommand : BaseCommand() {
    override val commandNames = listOf("/compiler", "/exec")

    private val kotlinCompilerServer = "https://api.kotlinlang.org/api/2.0.20/compiler/run"
    private val glotCompilerServer = "https://glot.io/run"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("使用`/exec` <语言> <代码> 即可执行~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val language = args.first()
        val code = args.drop(1).joinToString(" ")
        val response = when (language) {
            "kotlin", "Kotlin", "kt", "Kt", "KT" -> {
                Http.post<KCSResponse>(
                    kotlinCompilerServer, jsonBody = KCSPayload(listOf(KCSPayload.File(code))).toJson()
                ).text
            }

            else -> {
                Http.post<GLOTResponse>(
                    "$glotCompilerServer/$language",
                    params = mapOf("version" to "latest"),
                    jsonBody = GLOTPayload(listOf(GLOTPayload.File(code))).toJson()
                ).stdout
            }
        }
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addText("执行结果如下:")
            .addNewLine()
            .addText(response.replace("<outStream>", "").replace("</outStream>", ""))
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}