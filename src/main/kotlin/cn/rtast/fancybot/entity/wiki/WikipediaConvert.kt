/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.entity.wiki

data class WikipediaConvertPayload(val content: String)
data class WikipediaConvertResponse(val parsed: String)