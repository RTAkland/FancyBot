/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/7
 */


package cn.rtast.fancybot.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

object Logger {
    inline fun <reified T> getLogger(): Logger {
        return LoggerFactory.getLogger(T::class.simpleName).also { it.atLevel(Level.INFO) }
    }
}