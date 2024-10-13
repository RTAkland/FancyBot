/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/13
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.commands.misc.ShortLinkCommand.Companion.makeShortLink
import cn.rtast.fancybot.entity.douyin.DouyinVideo
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.misc.toBufferedImage
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.formatNumber
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage

object DouyinVideoParseCommand {

    private val tempHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    private val shortLinkRegex = Regex("https://v\\.douyin\\.com/[a-zA-Z0-9]+/")
    private val singleVideoRegex = Regex("https://www\\.douyin\\.com/video/([0-9]+)")
    private val douyinApiUrl = "https://douyin.wtf/api/douyin/web/fetch_one_video".proxy
    private const val IMAGE_WIDTH = 1200
    private const val IMAGE_HEIGHT = 800

    private fun extractVideoId(origin: String) = singleVideoRegex.find(origin)?.groupValues?.get(1)!!
    private fun createDouyinVideoCard(video: DouyinVideo): String {
        val card = BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g2d = card.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.color = Color(245, 245, 245)
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
        g2d.color = Color(230, 230, 230)
        g2d.fill(RoundRectangle2D.Float(20f, 20f, (IMAGE_WIDTH - 40).toFloat(), (IMAGE_HEIGHT - 40).toFloat(), 50f, 50f))
        g2d.color = Color.BLACK
        g2d.font = Font("Serif", Font.BOLD, 48)
        g2d.drawString(video.data.awemeDetail.author.nickname, 60, 100)
        g2d.font = Font("Serif", Font.PLAIN, 32)
        g2d.drawString(video.data.awemeDetail.desc, 60, 160)
        val avatarImage = video.data.awemeDetail.music.avatarLarge.urlList.first().toURL().readBytes().toBufferedImage()
        g2d.drawImage(avatarImage, 60, 200, 160, 160, null)
        val coverImage = video.data.awemeDetail.video.cover.urlList.first().toURL().readBytes().toBufferedImage()
        g2d.drawImage(coverImage, 300, 200, 600, 360, null)
        g2d.color = Color.DARK_GRAY
        g2d.font = Font("Serif", Font.PLAIN, 28)
        g2d.drawString("点赞: ${video.data.awemeDetail.statistics.diggCount.formatNumber()}", 60, 600)
        g2d.drawString("播放: ${video.data.awemeDetail.statistics.playCount.formatNumber()}", 60, 640)
        g2d.drawString("评论: ${video.data.awemeDetail.statistics.commentCount.formatNumber()}", 60, 680)
        g2d.drawString("收藏: ${video.data.awemeDetail.statistics.collectCount.formatNumber()}", 60, 720)
        g2d.drawString("分享: ${video.data.awemeDetail.statistics.shareCount.formatNumber()}", 60, 760)
        g2d.dispose()
        return card.toByteArray().encodeToBase64()
    }

    suspend fun parse(message: GroupMessage) {
        val id = if (shortLinkRegex.find(message.rawMessage) != null) {
            val shortUrl = shortLinkRegex.find(message.rawMessage)?.value ?: ""
            val request = Request.Builder().url(shortUrl).build()
            val finalUrl = tempHttpClient.newCall(request).execute().request.url.toString()
            extractVideoId(finalUrl)
        } else if (singleVideoRegex.find(message.rawMessage) != null) extractVideoId(message.rawMessage) else ""
        if (id.isNotBlank()) {
            val response = Http.get<DouyinVideo>(
                douyinApiUrl,
                mapOf("aweme_id" to id)
            )
            val videoPlayUrlShortUrl = response.data.awemeDetail.video.bitRate
                .random().playAddr.urlList.random().makeShortLink()
            val shareUrlShortUrl = response.data.awemeDetail.shareUrl.makeShortLink()
            val image = this.createDouyinVideoCard(response)
            val msg= MessageChain.Builder()
                .addImage(image, true)
                .addText("视频地址: $shareUrlShortUrl")
                .addNewLine()
                .addText("下载地址: $videoPlayUrlShortUrl")
                .build()
            message.reply(msg)
        }
    }
}