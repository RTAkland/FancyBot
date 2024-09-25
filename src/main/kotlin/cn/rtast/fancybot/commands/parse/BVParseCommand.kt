/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.entity.bili.ShortUrl
import cn.rtast.fancybot.entity.bili.UserStat
import cn.rtast.fancybot.entity.bili.VideoStat
import cn.rtast.fancybot.entity.bili.ViewCount
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.drawCustomImage
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.formatNumber
import cn.rtast.fancybot.util.str.setTruncate
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

object BVParseCommand {

    private const val VIDEO_STAT_URL = "https://api.bilibili.com/x/web-interface/view"
    private const val USER_STAT_URL = "https://api.bilibili.com/x/relation/stat"
    private const val SHORT_URL_API_URL = "https://api.bilibili.com/x/share/click"
    private const val VIEW_COUNT_URL = "https://api.bilibili.com/x/player/online/total"
    private const val CANVAS_WIDTH = 1000
    private const val CANVAS_HEIGHT = 600
    private val backgroundColor = Color(43, 43, 43)
    private val upBarColor = Color(102, 102, 102)
    private val textColor = Color.WHITE
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

    private fun generateShortUrl(bvid: String, oid: Long): String {
        val result = Http.post<ShortUrl>(
            SHORT_URL_API_URL, mapOf(
                "buvid" to bvid,
                "build" to 6114514,
                "platform" to "unix",
                "share_channel" to "COPY",
                "share_mode" to 2,
                "share_id" to "main.ugc-video-detail.0.0.pv",
                "oid" to oid
            )
        )
        return result.data.content.split(" ").last()
    }

    private fun getViewCount(bvid: String, cid: Long): String {
        return Http.get<ViewCount>(
            VIEW_COUNT_URL, mapOf(
                "bvid" to bvid,
                "cid" to cid
            )
        ).data.total
    }

    private fun getVideoStat(bvid: String): VideoStat {
        return Http.get<VideoStat>(VIDEO_STAT_URL, mapOf("bvid" to bvid))
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
        viewCount: String,
    ): String {
        val coverImage = ImageIO.read(URI(picUrl).toURL())
        val faceImage = ImageIO.read(URI(authorFace).toURL())
        val canvas = BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g2d = canvas.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = backgroundColor
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
        // draw rect
        g2d.color = upBarColor
        g2d.fillRect(0, 0, CANVAS_WIDTH, 120)
        g2d.color = textColor
        // draw numbers
        g2d.font = numberFont
        g2d.drawString(like, 110, 200)
        g2d.drawString(coin, 110, 290)
        g2d.drawString(view, 110, 380)
        g2d.drawString(favorite, 300, 380)
        g2d.drawString(reply, 300, 290)
        g2d.drawString(share, 300, 200)
        // draw video title
        val truncatedText = setTruncate(title, g2d)
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
        // draw viewing count
        g2d.drawString("${viewCount}人正在观看", 40, 460)
        // draw author face
        g2d.drawCustomImage(faceImage, 40, 480, 60.0, 60.0, true)
        // draw cover image
        g2d.drawCustomImage(coverImage, 430, 140, 600.0, 300.0, true)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }

    fun getShortUrlBVID(shortUrl: String): String {
        val request = Request.Builder().url(shortUrl.split(" ").last()).build()
        val redirectedUrl = tempOkHttpClient.newCall(request).execute()
        redirectedUrl.use {
            return it.request.url.toString().split("/")[4].split("?").first()
        }
    }

    suspend fun parse(listener: OneBotListener, bvid: String, message: GroupMessage) {
        val videoInfo = this.getVideoStat(bvid)
        val viewCount = this.getViewCount(bvid, videoInfo.data.cid)
        val shortUrl = this.generateShortUrl(bvid, videoInfo.data.aid)
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
            share, like, favorite, fans, viewCount
        )
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addImage(image, true)
            .addText(shortUrl)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}