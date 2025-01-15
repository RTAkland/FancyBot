/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/10
 */


package cn.rtast.fancybot.util.misc

import java.io.InputStream
import kotlin.jvm.javaClass

object Resources {
    fun loadFromResources(filename: String): InputStream? {
        return this::class.java.classLoader.getResourceAsStream(filename)
    }

    fun loadFromResourcesAsBytes(filename: String): ByteArray? {
        val inputStream = this::class.java.classLoader.getResourceAsStream(filename)
        return inputStream?.use { it.readBytes() }
    }
}
