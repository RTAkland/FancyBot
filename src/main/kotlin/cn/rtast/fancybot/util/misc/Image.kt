/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.util.misc

import cn.rtast.fancybot.configManager
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.random.Random


private fun BufferedImage.getScaledWidth(maxWidth: Double, maxHeight: Double): Pair<Int, Int> {
    val originalWidth = this.getWidth(null)
    val originalHeight = this.getHeight(null)
    val widthScale = maxWidth / originalWidth
    val heightScale = maxHeight / originalHeight
    val scale = minOf(widthScale, heightScale)
    val targetWidth = (originalWidth * scale).toInt()
    val targetHeight = (originalHeight * scale).toInt()
    return targetWidth to targetHeight
}

fun Graphics2D.drawCustomImage(
    image: BufferedImage,
    x: Int,
    y: Int,
    maxWidth: Double,
    maxHeight: Double,
    clip: Boolean = false,
) {
    val (targetWidth, targetHeight) = image.getScaledWidth(maxWidth, maxHeight)
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
    this.clip = null
}

fun Graphics2D.drawCircularImage(
    image: BufferedImage,
    x: Int,
    y: Int,
    maxWidth: Double,
    maxHeight: Double,
) {
    val (targetWidth, targetHeight) = image.getScaledWidth(maxWidth, maxHeight)
    val circle = Ellipse2D.Double(x.toDouble(), y.toDouble(), targetWidth.toDouble(), targetHeight.toDouble())
    this.clip = circle
    this.drawImage(image, x, y, targetWidth, targetHeight, null)
    this.clip = null
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

fun Graphics2D.drawString(text: String, x: Int, y: Int, maxWidth: Int) {
    val fm = this.fontMetrics
    val lineHeight = fm.height
    var curY = y
    val words = text.split(" ")
    val line = StringBuilder()
    for (word in words) {
        if (fm.stringWidth("$line$word ") <= maxWidth) {
            line.append(word).append(" ")
        } else {
            this.drawString(line.toString(), x, curY)
            curY += lineHeight
            line.setLength(0)
            line.append(word).append(" ")
        }
    }
    if (line.isNotEmpty()) {
        this.drawString(line.toString(), x, curY)
    }
}

fun BufferedImage.isFullyTransparent(): Boolean {
    val width = this.width
    val height = this.height
    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = this.getRGB(x, y)
            if ((pixel shr 24) != 0x00) {
                return false
            }
        }
    }
    return true
}

fun BufferedImage.toGrayscale(): BufferedImage {
    val width = this.width
    val height = this.height
    val grayscaleImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = Color(this.getRGB(x, y))
            val gray = (color.red * 0.299 + color.green * 0.587 + color.blue * 0.114).toInt()
            val grayColor = Color(gray, gray, gray)
            grayscaleImage.setRGB(x, y, grayColor.rgb)
        }
    }
    return grayscaleImage
}

fun BufferedImage.invertColor(): BufferedImage {
    val width = this.width
    val height = this.height
    for (x in 0 until width) {
        for (y in 0 until height) {
            val rgba = this.getRGB(x, y)
            val a = rgba shr 24 and 0xff
            val r = 255 - (rgba shr 16 and 0xff)
            val g = 255 - (rgba shr 8 and 0xff)
            val b = 255 - (rgba and 0xff)
            val invertedColor = (a shl 24) or (r shl 16) or (g shl 8) or b
            this.setRGB(x, y, invertedColor)
        }
    }
    return this
}

fun BufferedImage.randomColor(): BufferedImage {
    val width = this.width
    val height = this.height
    for (x in 0 until width) {
        for (y in 0 until height) {
            val rgba = this.getRGB(x, y)
            val a = (rgba shr 24) and 0xff
            val r = (rgba shr 16) and 0xff
            val g = (rgba shr 8) and 0xff
            val b = rgba and 0xff
            if (a < 255 || (r == 255 && g == 255 && b == 255) || (r == 0 && g == 0 && b == 0)) {
                continue
            }
            val newR = Random.nextInt(256)
            val newG = Random.nextInt(256)
            val newB = Random.nextInt(256)
            val randomColor = (a shl 24) or (newR shl 16) or (newG shl 8) or newB
            this.setRGB(x, y, randomColor)
        }
    }
    return this
}