/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/21
 */


package cn.rtast.fancybot.commands.parse

import javax.script.ScriptEngineManager
import javax.script.ScriptException

object CalculateCommand {

    private val engine = ScriptEngineManager().getEngineByName("JavaScript")

    fun parse(input: String): String? {
        return try {
            engine.eval(input)
        } catch (_: ScriptException) {
            return null
        }.toString()
    }
}