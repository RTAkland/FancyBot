/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/18
 */


package cn.rtast.fancybot.util.file

import cn.rtast.fancybot.util.JsonFileHandler

class BadWordManager: JsonFileHandler<List<String>>("bd.json", listOf<String>()) {

    fun contain(word: String): Boolean {
        return this.read<List<String>>().any { word.contains(it) }
    }
}