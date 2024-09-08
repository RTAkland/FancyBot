/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/8
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GetMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.madgag.gif.fmsware.GifDecoder
import java.io.ByteArrayOutputStream
import java.net.URI

object ReverseGIFCommand {

    private fun reverseGif(gifUrl: String): String {
        val gifStream = URI(gifUrl).toURL().openStream()
        val decoder = GifDecoder()
        decoder.read(gifStream)
        val frames = (0 until decoder.frameCount).map { decoder.getFrame(it) }.reversed()
        val byteArrayOutputStream = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.start(byteArrayOutputStream)
        encoder.setRepeat(0)
        for (i in frames.indices) {
            encoder.setDelay(decoder.getDelay(decoder.frameCount - i - 1))
            encoder.addFrame(frames[i])
        }
        encoder.finish()
        val gifBytes = byteArrayOutputStream.toByteArray()
        return gifBytes.encodeToBase64()
    }

    suspend fun callback(listener: OBMessage, message: GetMessage) {
        val gifUrl = if (message.data.message.find { it.type == ArrayMessageType.mface } == null) {
            message.data.message.find { it.type == ArrayMessageType.image }!!.data.file!!
        } else {
            message.data.message.find { it.type == ArrayMessageType.mface }!!.data.url!!
        }
        val base64String = this.reverseGif(gifUrl)
        val msg = MessageChain.Builder()
            .addReply(message.data.messageId)
            .addImage(base64String, true)
            .build()
        listener.sendGroupMessage(message.data.groupId!!, msg)
    }
}