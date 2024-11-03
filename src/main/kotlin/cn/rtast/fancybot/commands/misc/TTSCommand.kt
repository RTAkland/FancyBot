/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/7
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.tts.TTSResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.AIRecordCharacterType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import okhttp3.FormBody

@CommandDescription("将文字转换成语音(TTS)!")
class TTSCommand : BaseCommand() {
    override val commandNames = listOf("/tts")

    private val ttsApiUrl = "https://ttsmp3.com/makemp3_new.php"

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/tts <文字>`就可以生成语音啦~")
            return
        }
        val content = args.joinToString(" ").trim()
        val form = FormBody.Builder()
            .add("msg", content)
            .add("lang", "Zhiyu")
            .add("source", "ttsmp3")
            .build()
        val response = Http.post<TTSResponse>(ttsApiUrl, form)
        val msg = MessageChain.Builder()
            .addRecord(response.url.proxy)
            .build()
        message.reply(msg)
    }
}

class AITTSCommand : BaseCommand() {
    override val commandNames = listOf("/aitts")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ").trim()
        val character = if (args.size == 1) AIRecordCharacterType.XiaoXin
        else AIRecordCharacterType.forName(args.last()) ?: AIRecordCharacterType.XiaoXin
        message.action.sendGroupAIRecord(message.groupId, character, content)
    }
}