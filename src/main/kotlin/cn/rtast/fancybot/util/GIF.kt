/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/21
 */


package cn.rtast.fancybot.util

import com.madgag.gif.fmsware.AnimatedGifEncoder
import com.madgag.gif.fmsware.GifDecoder
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

fun GifDecoder.makeGif(frames: List<BufferedImage>): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val encoder = AnimatedGifEncoder()
    encoder.start(byteArrayOutputStream)
    encoder.setRepeat(0)
    for (i in frames.indices) {
        encoder.setDelay(this.getDelay(this.frameCount - i - 1))
        encoder.addFrame(frames[i])
    }
    encoder.finish()
    return byteArrayOutputStream.toByteArray()
}