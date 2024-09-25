/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.pixiv.Ranking
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
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
                .addAt(message.sender.userId)
                .addText("发送/pixiv <id> | rank 来进行操作哦~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        when (val keyword = args.first()) {
            "rank", "排名", "r" -> {
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addText("今日Pixiv Ranking~")
                    .addNewLine()

                val result = Http.get<Ranking>(pixivRankingURL)
                result.contents.asSequence().take(5).forEach {
                    try {
                        val imageBase64 = URI(it.getOriginUrl()).toURL().readBytes().encodeToBase64()
                        msg.addText(it.title)
                            .addImage(imageBase64, true)
                            .addNewLine()
                    } catch (_: Exception) {
                    }
                }
                listener.sendGroupMessage(message.groupId, msg.build())
                return
            }

            else -> {
                try {
                    val id = keyword.toLong()
                    val imageBase64 = URI("$imageProxyURL/$id.png").toURL().readBytes().encodeToBase64()
                    println(imageBase64)
                    val msg = MessageChain.Builder()
                        .addAt(message.sender.userId)
                        .addText("来咯~ 你要的图片~~~")
                        .addImage(imageBase64, true)
                        .build()
                    listener.sendGroupMessage(message.groupId, msg)
                } catch (_: Exception) {
                    val msg = MessageChain.Builder()
                        .addAt(message.sender.userId)
                        .addText("输入错误~检查一下有没有输错吧~")
                        .build()
                    listener.sendGroupMessage(message.groupId, msg)
                }
            }
        }
    }
}