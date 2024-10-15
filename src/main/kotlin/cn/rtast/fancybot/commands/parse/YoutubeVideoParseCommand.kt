/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/15
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.yt.YoutubeVideoResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.misc.toBufferedImage
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.misc.toURL
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

object YoutubeVideoParseCommand {
    private const val YOUTUBE_API_URL = "https://proxy.rtast.cn/https/www.googleapis.com/youtube/v3/videos"
    private const val IMAGE_WIDTH = 800
    private const val IMAGE_HEIGHT = 450
    private val dataApiKey = configManager.youtubeDataApiKey
    private val youtubeVideoIdRegex =
        Regex("(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:watch\\?v=|embed/|v/|.+\\?v=)|youtu\\.be/)([\\w\\-]{11})")

    private fun createVideoCard(videoInfo: YoutubeVideoResponse): String {
        val image = BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.color = Color(48, 48, 48)
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
        g2d.color = Color(255, 255, 255)
        g2d.font = Font("Serif", Font.BOLD, 24)
        val title = videoInfo.items.first().snippet.title
        g2d.drawString(title, 20, 40)
        g2d.font = Font("Serif", Font.PLAIN, 18)
        val channelTitle = videoInfo.items.first().snippet.channelTitle
        g2d.drawString(channelTitle, 20, 70)
        val thumbnailImage = videoInfo.items.first().snippet.thumbnails.standard.url
            .proxy.toURL().readBytes().toBufferedImage()
        val scaledWidth = 300
        val scaledHeight = (thumbnailImage.height * (scaledWidth / thumbnailImage.width.toDouble())).toInt()
        g2d.drawImage(thumbnailImage, 20, 100, scaledWidth, scaledHeight, null)
        g2d.color = Color(200, 200, 200)
        g2d.font = Font("Serif", Font.PLAIN, 16)
        g2d.drawString("观看次数: ${videoInfo.items.first().statistics.viewCount}", 340, 120)
        g2d.drawString("点赞次数: ${videoInfo.items.first().statistics.likeCount}", 340, 150)
        g2d.drawString("收藏次数: ${videoInfo.items.first().statistics.favoriteCount}", 340, 180)
        g2d.drawString("评论数: ${videoInfo.items.first().statistics.commentCount}", 340, 210)
        g2d.dispose()
        return image.toByteArray().encodeToBase64()
    }

    private fun getVideoInfo(videoId: String): YoutubeVideoResponse {
        val response = Http.get<YoutubeVideoResponse>(
            YOUTUBE_API_URL,
            mapOf(
                "id" to videoId,
                "key" to dataApiKey,
                "part" to "snippet,statistics"
            )
        )
        return response
    }

    suspend fun parse(message: GroupMessage) {
        val matchResult = youtubeVideoIdRegex.find(message.rawMessage)
        if (matchResult != null) {
            val videoId = matchResult.groupValues[1]
            val videoInfo = this.getVideoInfo(videoId)
            val image = createVideoCard(videoInfo)
            val msg = MessageChain.Builder().addImage(image, true).build()
            message.reply(msg)
        }
    }
}