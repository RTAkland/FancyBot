/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener
import kotlin.random.Random

@CommandDescription("今天吃什么")
class TodayEatCommand : BaseCommand() {
    override val commandNames = listOf("今天吃什么")

    private val foodList = listOf(
        "炒饭", "火锅", "汉堡", "披萨", "寿司", "拉面", "炸鸡", "米粉",
        "牛排", "烤串", "沙拉", "饺子", "意大利面", "烤鸭", "豆腐脑",
        "蛋糕", "烧烤", "咖喱饭", "麻辣烫", "烤鱼", "牛肉面", "凉皮",
        "西餐", "日式便当", "韩式拌饭", "生煎包", "炸酱面", "煎饼果子",
        "酸辣粉", "煲仔饭", "⑩", "史"
    )

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (KFCCommand.isThursday() && Random.nextBoolean()) {
            message.reply("今天是疯! 狂! 星! 期! 四!, 所以吃肯德基!")
            return
        }
        message.reply("今天吃${foodList.random()}吧~")
    }
}

@CommandDescription("今天喝什么")
class TodayDrinkCommand : BaseCommand() {
    override val commandNames = listOf("今天喝什么")

    private val drinkList = listOf(
        "咖啡", "绿茶", "奶茶", "橙汁", "苹果汁", "可乐", "啤酒", "矿泉水",
        "苏打水", "红茶", "酸梅汤", "豆浆", "椰子水", "冰沙", "葡萄汁",
        "柠檬水", "奶昔", "热巧克力", "果茶", "乌龙茶", "茉莉花茶", "白开水",
        "气泡水", "奶盖茶", "冰可乐", "草莓汁", "芒果汁", "蓝莓汁", "热奶茶", "蜂蜜柚子茶"
    )

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        message.reply("今天喝${drinkList.random()}吧~")
    }
}