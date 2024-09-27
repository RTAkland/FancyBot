/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.gpt.ChatCompletionsPayload
import cn.rtast.fancybot.entity.gpt.ChatCompletionsResponse
import cn.rtast.fancybot.entity.gpt.LlamaResponse
import cn.rtast.fancybot.entity.gpt.ModelList
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("问AI(GPT)")
class AICommand : BaseCommand() {
    override val commandNames = listOf("/ai")

    private val openAIModel = configManager.openAIModel

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("发送`/ai [模型] <问题>`即可询问AI哦~")
                .addNewLine()
                .addText("不指定模型默认为从配置文件中读取 >>>${openAIModel}")
                .addNewLine()
                .addText("发送`/ai list`可以获取可用的模型列表~")
                .build()
            message.reply(msg)
            return
        }

        if (args.first() == "列表" || args.first() == "list") {
            val models = Http.get<ModelList>(
                "${configManager.openAIAPIHost}/v1/models",
                headers = mapOf("Authorization" to "Bearer ${configManager.openAIAPIKey}")
            )
            val modelsString = models.data.joinToString(", ") { it.id }
            val msg = MessageChain.Builder()
                .addText("可用的模型列表如下: ")
                .addNewLine()
                .addText(modelsString)
                .build()
            message.reply(msg)
            return
        }

        val model = if (args.size == 1) openAIModel else args.first()
        val content = if (args.size == 1) args.joinToString(" ") else args.drop(1).joinToString(" ")
        val messages = ChatCompletionsPayload(model, listOf(ChatCompletionsPayload.Message(content)))
        val response = Http.post<ChatCompletionsResponse>(
            "${configManager.openAIAPIHost}/v1/chat/completions", messages.toJson(),
            mapOf("Authorization" to "Bearer ${configManager.openAIAPIKey}")
        )
        val nodeMsg = NodeMessageChain.Builder()
        val msg = MessageChain.Builder()
            .addText(response.choices.first().message.content)
            .build()
        nodeMsg.addMessageChain(msg, message.sender.userId)
        listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
    }
}

@CommandDescription("问AI(LLAMA)")
class LlamaCommand : BaseCommand() {
    override val commandNames = listOf("/llama")

    private val llamaURL = configManager.llamaUrl
    private val llamaModel = configManager.llamaModel

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/llama <问题>`即可使用llama模型来回复")
            return
        }
        val prompt = args.joinToString(" ")
        val payload = ChatCompletionsPayload(llamaModel, listOf(ChatCompletionsPayload.Message(prompt)))
        val response = Http.post<LlamaResponse>("$llamaURL/api/chat", payload.toJson())
        val nodeMsg = NodeMessageChain.Builder()
        val msg = MessageChain.Builder()
            .addText("AI回复如下:")
            .addNewLine()
            .addText(response.message.content)
            .build()
        nodeMsg.addMessageChain(msg, message.sender.userId)
        listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
    }
}