/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.pixiv.Ranking
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

@CommandDescription("获取Pixiv上的图片")
class PixivCommand : BaseCommand() {
    override val commandNames = listOf("/p")

    private val pixivRankingURL = "https://proxy.rtast.cn/https/www.pixiv.net/ranking.php?format=json&mode=daily&p=1"
    private val imageProxyURL = "https://pixiv.re"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("发送`/pixiv <id> | rank` 来进行操作哦~")
                .build()
            message.reply(msg)
            return
        }
        when (val keyword = args.first()) {
            "rank", "排名", "r" -> {
                val node = NodeMessageChain.Builder()
                    .addMessageChain(
                        MessageChain.Builder().addText("今日Pixiv Ranking~").build(),
                        message.sender.userId
                    )
                val result = Http.get<Ranking>(pixivRankingURL)
                result.contents.asSequence().take(5).forEach {
                    try {
                        val msg = MessageChain.Builder()
                        val imageBase64 = URI(it.getOriginUrl()).toURL().readBytes().encodeToBase64()
                        msg.addImage(imageBase64, true)
                            .addText("标题: ${it.title}")
                            .addNewLine()
                            .addText("用户: ${it.userName}(${it.userId}) | 作品ID: ${it.illustId}")
                            .addNewLine()
                            .addText("日期: ${it.date}")
                        node.addMessageChain(msg.build(), configManager.selfId)
                    } catch (_: Exception) {
                    }
                }
                node.addMessageChain(
                    MessageChain.Builder()
                        .addText("图片来源: Pixiv")
                        .build(), configManager.selfId
                )
                message.reply(node.build())
                return
            }

            else -> {
                try {
                    val id = keyword.toLong()
                    val imageBase64 = URI("$imageProxyURL/$id.png").toURL().readBytes().encodeToBase64()
                    val msg = MessageChain.Builder()
                        .addImage(imageBase64, true)
                        .addText("图片来源: Pixiv")
                        .build()
                    message.reply(msg)
                } catch (_: Exception) {
                    val msg = MessageChain.Builder().addText("输入错误~检查一下有没有输错吧~").build()
                    message.reply(msg)
                }
            }
        }
    }
}