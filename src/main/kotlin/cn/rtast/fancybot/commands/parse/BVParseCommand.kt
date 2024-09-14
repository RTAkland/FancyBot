/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.entity.bili.BVID
import cn.rtast.fancybot.entity.bili.UserStat
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.drawCustomImage
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.formatNumber
import cn.rtast.fancybot.util.str.setTruncat
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URI
import javax.imageio.ImageIO

object BVParseCommand {

    private const val CID_URL = "https://api.bilibili.com/x/web-interface/view"
    private const val USER_STAT_URL = "https://api.bilibili.com/x/relation/stat"
    private const val CANVAS_WIDTH = 1000
    private const val CANVAS_HEIGHT = 600
    private val titleFont = Font("Serif", Font.ITALIC, 40)
    private val numberFont = Font("Serif", Font.ITALIC, 25)
    private val twoTwoLogo = ImageIO.read(Resources.loadFromResources("bili/22-coin.png"))
    private val likeIcon = ImageIO.read(Resources.loadFromResources("bili/like.png"))
    private val coinIcon = ImageIO.read(Resources.loadFromResources("bili/coin.png"))
    private val viewIcon = ImageIO.read(Resources.loadFromResources("bili/view.png"))
    private val shareIcon = ImageIO.read(Resources.loadFromResources("bili/share.png"))
    private val favoriteIcon = ImageIO.read(Resources.loadFromResources("bili/favorite.png"))
    private val replyIcon = ImageIO.read(Resources.loadFromResources("bili/reply.png"))

    private val tempOkHttpClient = OkHttpClient()

    fun getShortUrlBVID(shortUrl: String): String {
        val request = Request.Builder().url(shortUrl.split(" ").last()).build()
        val redirectedUrl = tempOkHttpClient.newCall(request).execute()
        redirectedUrl.use {
            return it.request.url.toString().split("/")[4].split("?").first()
        }
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
        favorite: String,
        fans: Int,
    ): String {
        val coverImage = ImageIO.read(URI(picUrl).toURL())
        val faceImage = ImageIO.read(URI(authorFace).toURL())
        val backgroundImage = BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g2d = backgroundImage.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
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
        val truncatedText = setTruncat(title, g2d)
        g2d.font = titleFont
        g2d.drawString(truncatedText, 20, 70)
        // draw author name
        g2d.drawString(author, 40, 580)
        // draw icons
        g2d.drawCustomImage(favoriteIcon, 230, 340, 50.0, 50.0, false)
        g2d.drawCustomImage(replyIcon, 230, 250, 50.0, 50.0, false)
        g2d.drawCustomImage(shareIcon, 230, 160, 50.0, 50.0, false)
        g2d.drawCustomImage(viewIcon, 40, 340, 50.0, 50.0, false)
        g2d.drawCustomImage(coinIcon, 40, 250, 50.0, 50.0, false)
        g2d.drawCustomImage(likeIcon, 40, 160, 50.0, 50.0, false)
        // draw 22 logo
        g2d.drawCustomImage(twoTwoLogo, 800, 450, 90.0, 160.0, false)
        // draw fans count
        g2d.font = numberFont
        g2d.drawString(fans.formatNumber() + "粉丝", 120, 520)
        // draw author face
        g2d.drawCustomImage(faceImage, 40, 480, 60.0, 60.0, true)
        // draw cover image
        g2d.drawCustomImage(coverImage, 430, 140, 600.0, 300.0, true)
        g2d.dispose()
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(backgroundImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return imageBytes.encodeToBase64()
    }

    suspend fun parse(listener: OBMessage, bvid: String, message: GroupMessage) {
        val videoInfo = Http.get<BVID>(CID_URL, mapOf("bvid" to bvid))
        val fans = Http.get<UserStat>(USER_STAT_URL, mapOf("vmid" to videoInfo.data.owner.mid)).data.follower
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
            share, like, favorite, fans
        )
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addImage(image, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}