/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.enums.CountryList
import cn.rtast.fancybot.util.randomBooleanWithProbability
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage

class RemakeCommand : BaseCommand() {
    override val commandNames = listOf("/remake", "/重开", "remake", "重开")

    private val locations = listOf(
        "厕所", "首都", "农村", "市区",
        "大学", "树上", "医院"
    )

    private val roles = listOf(
        "男孩子", "女孩子", "小男娘", "伞兵",
        "人机", "114514", "是吧", "罕见",
        "鸽子", "鼠鼠", "化石", "狗狗"
    )

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val isSuccess = randomBooleanWithProbability(0.7)
        val msg = MessageChain.Builder().addReply(message.messageId)
        if (isSuccess) {
            val country = CountryList.getRandomCountry()
            val location = locations.random()
            val role = roles.random()
            msg.addText("重开成功! 你转生在${country.chineseName}的$location, 是一个$role")
        } else
            msg.addText("转生失败~")
        listener.sendGroupMessage(message.groupId, msg.build())
    }
}