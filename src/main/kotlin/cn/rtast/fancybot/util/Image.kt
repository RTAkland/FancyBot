/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.configManager
import java.awt.Graphics2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun Graphics2D.drawCustomImage(
    image: BufferedImage,
    x: Int,
    y: Int,
    maxWidth: Double,
    maxHeight: Double,
    clip: Boolean = false
) {
    val originalWidth = image.getWidth(null)
    val originalHeight = image.getHeight(null)
    val widthScale = maxWidth / originalWidth
    val heightScale = maxHeight / originalHeight
    val scale = minOf(widthScale, heightScale)
    val targetWidth = (originalWidth * scale).toInt()
    val targetHeight = (originalHeight * scale).toInt()
    if (clip) {
        this.clip = RoundRectangle2D.Float(
            x.toFloat(),
            y.toFloat(),
            targetWidth.toFloat(),
            targetHeight.toFloat(),
            30.toFloat(),
            30.toFloat()
        )
    }
    this.drawImage(image, x, y, targetWidth, targetHeight, null)
}

fun BufferedImage.scaleImage(size: Pair<Int, Int>): BufferedImage {
    val scaledImage = BufferedImage(size.first, size.second, this.type)
    val graphics2d = scaledImage.createGraphics()
    graphics2d.drawImage(this, 0, 0, size.first, size.second, null)
    graphics2d.dispose()
    return scaledImage
}

fun ByteArray.toBufferedImage(): BufferedImage {
    ByteArrayInputStream(this).use { inputStream ->
        return ImageIO.read(inputStream)
    }
}

fun BufferedImage.toByteArray(): ByteArray {
    ByteArrayOutputStream().use { outputStream ->
        ImageIO.write(this, configManager.imageType.typeName, outputStream)
        return outputStream.toByteArray()
    }
}

fun Graphics2D.drawCenteredText(text: String, x: Int, y: Int) {
    val metrics = this.fontMetrics
    val textWidth = metrics.stringWidth(text)
    val textHeight = metrics.height
    val drawX = x - textWidth / 2
    val drawY = y + textHeight / 2
    this.drawString(text, drawX, drawY)
}