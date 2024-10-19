/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/19
 */


package fancybot

import cn.rtast.fancybot.util.misc.toURL
import com.madgag.gif.fmsware.GifDecoder

fun main() {
    val gifStream =
        "https://multimedia.nt.qq.com.cn/download?appid=1407&fileid=CgozNDU4NjcxMzk1EhQGV4kzYE1NcjH6H6u_yQs5zseDEximpgMg_woowZyctJeaiQMyBHByb2RQgL2jAQ&rkey=CAISKHim-nm2GSiHCH-8dNYw0c9FFONy0UTMoRzwJVH-z308_Bhz-_hrLCI"
            .toURL().openStream()
    val decoder = GifDecoder()
    decoder.read(gifStream)
    val frames = (0 until decoder.frameCount).map { decoder.getFrame(it) }
    println(frames.size)
}