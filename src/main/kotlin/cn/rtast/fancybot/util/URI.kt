/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.util

import java.net.URI
import java.net.URL

fun String.toURL(): URL {
    return URI(this).toURL()
}

fun String.toURI(): URI {
    return URI(this)
}