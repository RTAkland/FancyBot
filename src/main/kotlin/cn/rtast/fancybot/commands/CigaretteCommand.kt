/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.cigarette.Cigarette
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class CigaretteCommand : BaseCommand() {
    override val commandNames = listOf("/香烟", "/tobacco", "/cigarette")

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addNewLine()
                .addText("发送/tobacco <名称> [数量] 来搜索结果哦~")
                .addNewLine()
                .addText("最多只能显示出10条结果哦, 不指定数量的话默认是3条~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val limit = if (args.size == 1) 3 else if (args.last().toInt() > 10) 3 else args.last().toInt()
        val productName = args.first()
        val result = Http.get<Cigarette>(
            "https://www.yanyue.cn/api/rc/product/yanlist",
            mapOf("productname" to productName)
        )
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addNewLine()
            .addText("搜索到${result.totalCount}条结果, 仅展示前 $limit 条结果哦~")
            .addNewLine()

        result.productList.asSequence().take(limit).forEach {
            msg.addImage(it.cover)
                .addText("名称: ${it.productName} | 类型: ${it.type.typeName}")
                .addNewLine()
                .addText("焦油量: ${it.tar}mg | 尼古丁含量: ${it.nicotine}mg")
                .addNewLine()
        }
        listener.sendGroupMessage(message.groupId, msg.build())
    }
}