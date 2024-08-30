/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.cmd

import cn.rtast.fancybot.DEFAULT_CONFIG
import cn.rtast.fancybot.util.toJson
import java.io.File

fun main() {
    val file = File("src/main/resources/config.json")
    file.writeText(DEFAULT_CONFIG.toJson())
    println("Generated default config")
}