/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/11
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
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

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (KFCCommand.isThursday() && Random.nextBoolean()) {
            message.reply("今天是疯! 狂! 星! 期! 四!, 所以吃肯德基!")
            return
        }
        message.reply("今天吃${foodList.random()}吧~")
    }
}

@CommandDescription("今天不吃什么")
class TodayDontEatCommand : BaseCommand() {
    override val commandNames = listOf("今天不吃什么")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.reply("你吃个结巴, 是吧")
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

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.reply("今天喝${drinkList.random()}吧~")
    }
}

@CommandDescription("今天不吃什么")
class TodayDontDrinkCommand : BaseCommand() {
    override val commandNames = listOf("今天不喝什么")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.reply("你喝个结巴, 是吧")
    }
}

@CommandDescription("今天抽什么")
class TodaySmokeCommand : BaseCommand() {
    override val commandNames = listOf("今天抽什么")

    private val smokeList = listOf(
        "黑利", "蓝利", "白利", "普皖", "金皖",
        "宽窄", "煊赫门", "骆驼", "百乐(红酒)",
        "手卷烟", "烟斗", "万宝路", "白沙3代",
        "白沙2代", "中华", "南京-大观园", "金钗",
        "银钗", "玉溪", "芒果", "南京-雨花石",
        "金圣", "南京95", "红塔山", "轿子",
        "钻石荷花", "黄金叶(天叶)", "芙蓉王",
        "黄鹤楼", "红河", "长白山", "长丰",
        "中南海(蓝莓爆珠)", "云烟", "真龙", "红利"
    )

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        message.reply("今天抽${smokeList.random()}吧~")
    }
}
