/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.cigarette.Cigarette
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.net.URI

@CommandDescription("查询香烟价格")
class CigaretteCommand : BaseCommand() {
    override val commandNames = listOf("/tobacco")

    private val apiUrl = "https://www.yanyue.cn/api/rc/product/yanlist"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("发送/tobacco <名称> [数量] 来搜索结果哦~")
                .addNewLine()
                .addText("最多只能显示出10条结果哦, 不指定数量的话默认是3条~")
                .build()
            message.reply(msg)
            return
        }
        val limit = if (args.size == 1) 3 else if (args.last().toInt() > 10) 3 else args.last().toInt()
        val productName = args.first()
        val result = Http.get<Cigarette>(apiUrl, mapOf("productname" to productName))
        val nodeMsg = NodeMessageChain.Builder()
        val headerMsg = MessageChain.Builder()
            .addText("搜索到${result.totalCount}条结果, 仅展示前 $limit 条结果哦~")
            .build()
        nodeMsg.addMessageChain(headerMsg, message.sender.userId)
        result.productList.asSequence().take(limit).forEach {
            val imageBase64 = URI(it.cover).toURL().readBytes().encodeToBase64()
            val tempMsg = MessageChain.Builder()
                .addImage(imageBase64, true)
                .addText("名称: ${it.productName} | 类型: ${it.type.typeName}")
                .addNewLine()
                .addText("焦油量: ${it.tar}mg | 尼古丁含量: ${it.nicotine}mg")
                .addNewLine()
                .addText("参考价: 单盒: ${it.packPrice}元 | 整条: ${it.barPrice}元")
                .addNewLine()
                .build()
            nodeMsg.addMessageChain(tempMsg, configManager.selfId)
        }
        val footerMsg = MessageChain.Builder()
            .addText("吸烟有害健康, 尽早戒烟有益健康。")
            .build()
        nodeMsg.addMessageChain(footerMsg, configManager.selfId)
        message.reply(nodeMsg.build())
        message.sender.poke()
    }
}