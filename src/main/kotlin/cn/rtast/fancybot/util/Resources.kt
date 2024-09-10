/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/10
 */


package cn.rtast.fancybot.util

import java.io.InputStream

object Resources {
    fun loadFromResources(filename: String): InputStream {
        return this::class.java.classLoader.getResourceAsStream(filename)
    }
}
