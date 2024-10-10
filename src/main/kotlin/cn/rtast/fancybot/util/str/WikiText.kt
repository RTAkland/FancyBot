/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.util.str

import info.bliki.wiki.model.WikiModel

fun String.convertToHTML(): String {
    return WikiModel.toHtml(this)
}