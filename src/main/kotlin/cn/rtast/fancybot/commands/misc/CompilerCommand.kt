/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.compiler.GLOTPayload
import cn.rtast.fancybot.entity.compiler.GLOTResponse
import cn.rtast.fancybot.entity.compiler.KCSPayload
import cn.rtast.fancybot.entity.compiler.KCSResponse
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("执行不同语言的代码")
class CompilerCommand : BaseCommand() {
    override val commandNames = listOf("/compiler", "/exec", "/e")

    private val kotlinCompilerServer = "https://api.kotlinlang.org/api/2.0.20/compiler/run"
    private val glotCompilerServer = "https://glot.io/run"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("使用`/exec` <语言> <代码> 即可执行~")
            return
        }
        var language = args.first().lowercase()
        val code = args.drop(1).joinToString(" ")
        val response = when (language) {
            "kotlin", "kt" -> {
                Http.post<KCSResponse>(
                    kotlinCompilerServer,
                    jsonBody = KCSPayload(listOf(KCSPayload.File(code))).toJson()
                ).text
            }

            else -> {
                if (language == "js") language = "javascript"
                if (language == "cs") language = "csharp"
                if (language == "sh") language = "bash"
                if (language == "asm") language = "assembly"
                if (language == "py") language = "python"
                Http.post<GLOTResponse>(
                    "$glotCompilerServer/$language",
                    params = mapOf("version" to "latest"),
                    jsonBody = GLOTPayload(listOf(GLOTPayload.File(code))).toJson()
                ).stdout
            }
        }
        try {
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("执行结果如下:")
                .addNewLine()
                .addText(response.replace("<outStream>", "").replace("</outStream>", ""))
                .build()
            message.reply(msg)
        } catch (e: Exception) {
            message.reply("执行错误: ${e.message}")
        } finally {
            insertActionRecord(CommandAction.Compiler, message.sender.userId)
        }
    }
}