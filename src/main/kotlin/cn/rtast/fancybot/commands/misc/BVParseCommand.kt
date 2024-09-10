/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.entity.bili.BVID
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URI
import javax.imageio.ImageIO

object BVParseCommand {

    private const val CID_URL = "https://api.bilibili.com/x/web-interface/view"
    private const val CANVAS_WIDTH = 1000
    private const val CANVAS_HEIGHT = 600
    private val titleFont = Font("Serif", Font.ITALIC, 40).deriveFont(Font.ITALIC)
    private val numberFont = Font("Serif", Font.ITALIC, 25).deriveFont(Font.ITALIC)
    private val twoTwoLogo = ImageIO.read(Resources.loadFromResources("bili/22-coin.png"))
    private val likeIcon = ImageIO.read(Resources.loadFromResources("bili/like.png"))
    private val coinIcon = ImageIO.read(Resources.loadFromResources("bili/coin.png"))
    private val viewIcon = ImageIO.read(Resources.loadFromResources("bili/view.png"))
    private val shareIcon = ImageIO.read(Resources.loadFromResources("bili/share.png"))
    private val favoriteIcon = ImageIO.read(Resources.loadFromResources("bili/favorite.png"))
    private val replyIcon = ImageIO.read(Resources.loadFromResources("bili/reply.png"))

    private fun Int.formatNumber(): String {
        return when {
            this >= 100000000 -> "${this / 100000000}亿"
            this >= 10000 -> {
                val wan = this / 10000
                val remainder = this % 10000
                if (remainder == 0) "${wan}万" else "${wan}万${remainder}"
            }

            this >= 1000 -> {
                val remainder = this % 1000
                if (remainder == 0) "${this / 1000}千" else "${this / 1000}千零${remainder}"
            }

            else -> this.toString()
        }
    }

    private fun drawImage(
        image: BufferedImage,
        g2d: Graphics2D,
        x: Int,
        y: Int,
        maxWidth: Double,
        maxHeight: Double,
        clip: Boolean
    ) {
        val originalWidth = image.getWidth(null)
        val originalHeight = image.getHeight(null)
        val widthScale = maxWidth / originalWidth
        val heightScale = maxHeight / originalHeight
        val scale = minOf(widthScale, heightScale)
        val targetWidth = (originalWidth * scale).toInt()
        val targetHeight = (originalHeight * scale).toInt()
        if (clip) {
            g2d.clip = RoundRectangle2D.Float(
                x.toFloat(),
                y.toFloat(),
                targetWidth.toFloat(),
                targetHeight.toFloat(),
                30.toFloat(),
                30.toFloat()
            )
        }
        g2d.drawImage(image, x, y, targetWidth, targetHeight, null)
    }

    private fun createResponseImage(
        title: String,
        author: String,
        authorFace: String,
        picUrl: String,
        reply: String,
        view: String,
        coin: String,
        share: String,
        like: String,
        favorite: String
    ): String {
        val coverImage = ImageIO.read(URI(picUrl).toURL())
        val faceImage = ImageIO.read(URI(authorFace).toURL())
        val backgroundImage = BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g2d = backgroundImage.createGraphics()
        g2d.color = Color(43, 43, 43)
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
        // draw rect
        g2d.color = Color(102, 102, 102)
        g2d.fillRect(0, 0, CANVAS_WIDTH, 120)
        g2d.color = Color.WHITE
        // draw numbers
        g2d.font = numberFont
        g2d.drawString(like, 110, 200)
        g2d.drawString(coin, 110, 290)
        g2d.drawString(view, 110, 380)
        g2d.drawString(favorite, 300, 380)
        g2d.drawString(reply, 300, 290)
        g2d.drawString(share, 300, 200)
        // draw video title
        val maxWidth = 500
        val fontMetrics = g2d.fontMetrics
        val textWidth = fontMetrics.stringWidth(title)
        val truncatedText = if (textWidth > maxWidth) {
            val ellipsisWidth = fontMetrics.stringWidth("...")
            val width = maxWidth - ellipsisWidth
            var endIndex = title.length
            while (fontMetrics.stringWidth(title.substring(0, endIndex)) > width && endIndex > 0) {
                endIndex--
            }
            title.substring(0, endIndex) + "..."
        } else title
        g2d.font = titleFont
        g2d.drawString(truncatedText, 20, 70)
        // draw author name
        g2d.drawString(author, 40, 580)
        // draw icons
        this.drawImage(favoriteIcon, g2d, 230, 340, 50.0, 50.0, false)
        this.drawImage(replyIcon, g2d, 230, 250, 50.0, 50.0, false)
        this.drawImage(shareIcon, g2d, 230, 160, 50.0, 50.0, false)
        this.drawImage(viewIcon, g2d, 40, 340, 50.0, 50.0, false)
        this.drawImage(coinIcon, g2d, 40, 250, 50.0, 50.0, false)
        this.drawImage(likeIcon, g2d, 40, 160, 50.0, 50.0, false)
        // draw 22 logo
        this.drawImage(twoTwoLogo, g2d, 800, 450, 90.0, 160.0, false)
        // draw author face
        this.drawImage(faceImage, g2d, 40, 480, 60.0, 60.0, true)
        // draw cover image
        this.drawImage(coverImage, g2d, 470, 140, 600.0, 300.0, true)
        g2d.dispose()
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(backgroundImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return imageBytes.encodeToBase64()
    }

    suspend fun parse(listener: OBMessage, message: GroupMessage) {
        val bvid = if (message.rawMessage.startsWith("BV")) message.rawMessage
        else message.rawMessage.split("/")[4]
        val videoInfo = Http.get<BVID>(CID_URL, mapOf("bvid" to bvid))
        val authorFace = videoInfo.data.owner.face
        val title = videoInfo.data.title
        val author = videoInfo.data.owner.name
        val coverPicUrl = videoInfo.data.pic
        val view = videoInfo.data.stat.view.formatNumber()
        val share = videoInfo.data.stat.share.formatNumber()
        val like = videoInfo.data.stat.like.formatNumber()
        val favorite = videoInfo.data.stat.favorite.formatNumber()
        val coin = videoInfo.data.stat.coin.formatNumber()
        val reply = videoInfo.data.stat.reply.formatNumber()
        val image = this.createResponseImage(
            title, author, authorFace,
            coverPicUrl, reply, view, coin,
            share, like, favorite
        )
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addImage(image, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}