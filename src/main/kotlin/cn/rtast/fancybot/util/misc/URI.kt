/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.util.misc

import java.net.URI
import java.net.URL

private val urlRegex = Regex(
    "^(https?://)?" +
            "([\\w\\-]+\\.)+[a-zA-Z]{2,6}" +
            "|(https?://)?" +
            "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
            "(:\\d{1,5})?" +
            "(/.*)?$"
)

fun String.toURL(): URL {
    return URI(this).toURL()
}

fun String.toURI(): URI {
    return URI(this)
}

fun String.isValidUrl(): Boolean {
    return urlRegex.matches(this)
}