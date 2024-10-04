/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.bullshit.BullShit
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.str.fromJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.NodeMessageChain
import cn.rtast.rob.util.ob.OneBotListener
import kotlin.random.Random

@CommandDescription("狗屁不通文章生成器")
class BullShitGenerateCommand : BaseCommand() {
    override val commandNames = listOf("/gpbt", "狗屁不通")

    private val bullShitData = String(Resources.loadFromResourcesAsBytes("misc/bull_shit.json")!!)
        .fromJson<BullShit>()


    private fun <T> shuffleList(list: List<T>): Iterator<T> {
        val pool = mutableListOf<T>().apply { addAll(list) }.toMutableList()
        repeat(1) { pool.addAll(list) }
        return generateSequence {
            pool.shuffle()
            pool
        }.flatten().iterator()
    }

    private fun getFamousQuote(nextQuote: Iterator<String>, beforeList: List<String>, afterList: List<String>): String {
        var quote = nextQuote.next()
        quote = quote.replace("a", beforeList.random())
        quote = quote.replace("b", afterList.random())
        return quote
    }

    private fun newParagraph(): String {
        return ".\n\n    "
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val topic = args.joinToString(" ")
        val nextFamousQuote = shuffleList(bullShitData.famous)
        val nextBosh = shuffleList(bullShitData.bosh)
        val output = StringBuilder()
        while (output.length < 6000) {
            val branch = Random.nextInt(100)
            if (branch < 5) {
                output.append(newParagraph())
            } else if (branch < 20) {
                output.append(getFamousQuote(nextFamousQuote, bullShitData.before, bullShitData.after))
            } else {
                output.append(nextBosh.next())
            }
        }
        val finalText = output.toString().replace("x", topic)
        val node = NodeMessageChain.Builder()
            .addMessageChain(MessageChain.Builder().addText(finalText).build(), configManager.selfId)
            .build()
        message.reply(node)
        message.sender.poke()
    }
}