/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */


package cn.rtast.fancybot.util.str

import cn.rtast.fancybot.API_RTAST_URL
import cn.rtast.fancybot.entity.wiki.WikipediaConvertPayload
import cn.rtast.fancybot.entity.wiki.WikipediaConvertResponse
import cn.rtast.fancybot.util.Http

fun String.convertToHTML(): String {
    val payload = WikipediaConvertPayload(this)
    val response = Http.post<WikipediaConvertResponse>(
        "$API_RTAST_URL/api/wikitext",
        payload.toJson()
    )
    return response.parsed
}