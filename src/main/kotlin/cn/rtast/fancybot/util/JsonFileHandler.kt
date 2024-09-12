/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/29
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.ROOT_PATH
import cn.rtast.fancybot.gson
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.fancybot.util.str.fromJson
import java.io.File

abstract class JsonFileHandler<T> {

    val file: File

    constructor(filename: String) {
        this.file = File(ROOT_PATH, filename)
        initFile(null)
    }

    constructor(filename: String, defaultConfig: T) {
        this.file = File(ROOT_PATH, filename)
        initFile(defaultConfig)
    }

    private fun initFile(defaultConfig: T?) {
        val configPath = File(ROOT_PATH)
        configPath.mkdirs()

        if (this.file.createNewFile()) {
            if (defaultConfig != null) {
                this.write(defaultConfig)
            } else {
                val resourceConfig = this::class.java.classLoader
                    .getResourceAsStream(file.name)
                    ?.reader()?.readText()!!
                this.write(resourceConfig)
            }
        }
    }

    private fun write(data: String) {
        this.file.writeText(data)
    }

    fun write(data: T) {
        this.write(gson.toJson(data))
    }

    inline fun <reified T> read(): T {
        val content = this.file.readText()
        return content.fromJson()
    }

    inline fun <reified T> readArray(): T {
        val content = this.file.readText()
        return content.fromArrayJson<T>()
    }
}