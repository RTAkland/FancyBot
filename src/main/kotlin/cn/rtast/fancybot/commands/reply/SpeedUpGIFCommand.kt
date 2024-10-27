/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/27
 */


package cn.rtast.fancybot.commands.reply

import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.commands.parse.AsciiArtCommand
import cn.rtast.fancybot.util.misc.ImageBed
import cn.rtast.fancybot.util.misc.makeGif
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import com.madgag.gif.fmsware.GifDecoder

object SpeedUpGIFCommand {
    suspend fun speedUp(message: GroupMessage, getMsg: GetMessage.Message, multiply: Float) {
        val gifUrl = AsciiArtCommand.getImageUrl(getMsg)
        val gifStream = gifUrl.toURL().openStream()
        val decoder = GifDecoder()
        decoder.read(gifStream)
        val frames = (0 until decoder.frameCount).map { decoder.getFrame(it) }
        val gifBase64 = decoder.makeGif(frames, multiply)
        val imgBedUrl = ImageBed.upload(gifBase64)
        val shortLink = imgBedUrl.makeShortLink()
        val msg = MessageChain.Builder()
            .addImage(gifBase64.encodeToBase64(), true)
            .addText(shortLink)
            .build()
        message.reply(msg)
    }
}