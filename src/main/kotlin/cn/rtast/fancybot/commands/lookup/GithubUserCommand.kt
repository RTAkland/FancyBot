/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/15
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.github.UserInfo
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.drawCustomImage
import cn.rtast.fancybot.util.drawString
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.toBufferedImage
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.net.URI

@CommandDescription("获取Github用户信息")
class GithubUserCommand : BaseCommand() {
    override val commandNames = listOf("/gh")

    private val userInfoUrl = "https://api.github.com/users"
    private val backgroundColor = Color(255, 255, 240)
    private val fontColor = Color.BLACK
    private val font = Font("Serif", Font.PLAIN, 35).deriveFont(Font.ITALIC or Font.BOLD)
    private val canvasWidth = 700
    private val canvasHeight = 600


    private fun createUserInfoCard(userInfo: UserInfo): String {
        val userAvatar = URI(userInfo.avatarUrl).toURL().readBytes().toBufferedImage()
        val canvas = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = canvas.createGraphics()
        g2d.color = backgroundColor
        g2d.fillRect(0, 0, canvasWidth, canvasHeight)
        g2d.font = font
        g2d.color = fontColor
        g2d.drawString(userInfo.login, 45, 260)
        g2d.drawString("关注数: ${userInfo.following}", 45, 310)
        g2d.drawString("被关注数: ${userInfo.followers}", 45, 360)
        userInfo.bio?.let {
            g2d.drawString("个人简介/Bio:", 240, 90)
            g2d.drawString(userInfo.bio, 240, 130, 500)
        }
        userInfo.location?.let {
            g2d.drawString("地区: ${userInfo.location}", 40, 410)
        }
        userInfo.email?.let {
            g2d.drawString("邮箱: ${userInfo.email}", 40, 460)
        }
        userInfo.company?.let {
            g2d.drawString("组织: ${userInfo.company}", 40, 510)
        }
        g2d.drawString("114514/1919810", 40, 560)
        g2d.drawCustomImage(userAvatar, 40, canvasHeight / 2 - 250, 170.0, 170.0, true)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`gh <用户名>`即可查询用户的信息~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val username = args.first()
        val userInfo = Http.get<UserInfo>(
            "$userInfoUrl/$username",
            headers = mapOf("Authorization" to configManager.githubKey)
        )
        val image = this.createUserInfoCard(userInfo)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addImage(image, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}
