/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/24
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.entity.bgm.BGMSearch
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import okhttp3.OkHttpClient
import okhttp3.Request

class DMSearchCommand : BaseCommand() {
    override val commandNames = listOf("/dm", "动漫", "dm")

    private val client = OkHttpClient()

    companion object {
        private const val API_URL = "https://api.bgm.tv/search/subject/#{}?type=2&responseGroup=small"
    }

    private fun followRedirect(url: String): ByteArray {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            return response.body.bytes()
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/dm <关键词>`即可搜索番剧啦~")
            return
        }
        try {
            val keyword = args.joinToString(" ")
            val response = Http.get<BGMSearch>(API_URL.replace("#{}", keyword)).list
            val nodeMsg = NodeMessageChain.Builder()
            response.asSequence().take(5).forEach {
                val imageBase64 = this.followRedirect(it.images.large).encodeToBase64()
                val tempMsg = MessageChain.Builder()
                    .addImage(imageBase64, true)
                    .addText("${it.name} | ${it.nameCN}")
                    .addNewLine()
                    .addText(it.url)
                    .build()
                nodeMsg.addMessageChain(tempMsg, message.sender.userId)
            }
            val footerNode = MessageChain.Builder().addText("数据来源: Bangumi").build()
            nodeMsg.addMessageChain(footerNode, message.sender.userId)
            listener.sendGroupForwardMsg(message.groupId, nodeMsg.build())
        } catch (_: Exception) {
            message.reply("没有搜索到结果呢~")
        }
    }
}