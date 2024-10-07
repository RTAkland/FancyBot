/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/5
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.str.decodeToString
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("将人类的语言翻译成尊嘟假嘟语还能将尊嘟假嘟语翻译成人类的语言!")
class ZDJDCommand : BaseCommand() {
    override val commandNames = listOf("尊嘟假嘟", "zdjd")

    private val b64 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/="
    private val leftEye = listOf("o", "0", "O", "Ö")
    private val mouse = listOf("w", "v", ".", "_")
    private val rightEye = listOf("o", "0", "O", "Ö")
    private val table = mutableListOf<String>()
    private val separator = " "

    init {
        makeTable()
    }

    private fun makeTable() {
        for (i in leftEye.indices) {
            for (j in mouse.indices) {
                for (k in rightEye.indices) {
                    table.add(leftEye[i] + mouse[j] + rightEye[k])
                }
            }
        }
    }

    private fun human2zdjd(text: String): String {
        val encoded = text.encodeToBase64()
        val arr = mutableListOf<String>()
        for (c in encoded) {
            if (c != '=') {
                val n = b64.indexOf(c)
                arr.add(table[n])
            }
        }
        return arr.joinToString(separator)
    }

    private fun zdjd2human(t: String): String {
        val arr = t.split(separator)
        val resultArr = mutableListOf<String>()
        for (c in arr) {
            if (c.isEmpty()) continue
            val n = table.indexOf(c)
            if (n < 0) throw IllegalArgumentException("Invalid zdjd code")
            resultArr.add(b64[n].toString())
        }
        var base64String = resultArr.joinToString("")
        val padding = resultArr.size % 4
        if (padding > 0) {
            base64String += "=".repeat(4 - padding)
        }
        return base64String.decodeToString()
    }

    private fun String.isZdjd(): Boolean {
        return try {
            zdjd2human(this)
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val text = if (message.message.any { it.type == ArrayMessageType.image }) {
            message.message.find { it.type == ArrayMessageType.image }!!.data.file!!
        } else args.joinToString(" ")
        if (text.isZdjd()) {
            message.reply(zdjd2human(text))
        } else {
            message.reply(human2zdjd(text))
        }
    }
}