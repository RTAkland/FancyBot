/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.gpt.ChatCompletionsPayload
import cn.rtast.fancybot.entity.gpt.ChatCompletionsResponse
import cn.rtast.fancybot.entity.gpt.ModelList
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

class GPTCommand : BaseCommand() {
    override val commandNames = listOf("/gpt")

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`/gpt [模型] <问题>`即可询问AI哦~")
                .addNewLine()
                .addText("不指定模型默认为`moonshot-v1-8k`")
                .addNewLine()
                .addText("发送`/gpt list`可以获取可用的模型列表~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }

        if (args.first() == "列表" || args.first() == "list") {
            val models = Http.get<ModelList>(
                "${configManager.openAIAPIHost}/v1/models",
                headers = mapOf("Authorization" to "Bearer ${configManager.openAIAPIKey}")
            )
            val modelsString = models.data.joinToString(", ") { it.id }
            val msg = MessageChain.Builder()
                .addReply(message.messageId)
                .addText("可用的模型列表如下: ")
                .addNewLine()
                .addText(modelsString)
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }

        val model = if (args.size == 1) "moonshot-v1-8k" else args.first()
        val content = if (args.size == 1) args.joinToString(" ") else args.drop(1).joinToString(" ")
        val messages = ChatCompletionsPayload(model, listOf(ChatCompletionsPayload.Message(content)))
        val response = Http.post<ChatCompletionsResponse>(
            "${configManager.openAIAPIHost}/v1/chat/completions",
            messages.toJson(),
            mapOf("Authorization" to "Bearer ${configManager.openAIAPIKey}")
        )
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText(response.choices.first().message.content)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}