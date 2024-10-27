/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/21
 */


package cn.rtast.fancybot.util.misc

import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.madgag.gif.fmsware.GifDecoder
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

fun GifDecoder.makeGif(frames: List<BufferedImage>, speed: Float? = null): ByteArray {
    val estimatedSize = frames.size * 50 * 1024
    val byteArrayOutputStream = ByteArrayOutputStream(estimatedSize)
    val encoder = AnimatedGifEncoder()
    encoder.start(byteArrayOutputStream)
    encoder.setRepeat(0)
    val delays = frames.indices.map { this.getDelay(this.frameCount - it - 1) }
    frames.indices.forEach { i ->
        if (speed == null) {
            encoder.setDelay(delays[i])
        } else {
            var delay = this.getDelay(i)
            delay = (delay / speed).toInt()
            encoder.setDelay(delay.coerceAtLeast(1))
        }
        encoder.addFrame(frames[i])
    }
    encoder.finish()
    return byteArrayOutputStream.toByteArray()
}