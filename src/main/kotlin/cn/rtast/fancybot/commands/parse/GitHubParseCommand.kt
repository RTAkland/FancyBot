/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.commands.parse

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.github.RepoInfo
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.Resources
import cn.rtast.fancybot.util.drawCustomImage
import cn.rtast.fancybot.util.drawString
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.formatNumberEnglish
import cn.rtast.fancybot.util.str.setTruncat
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

object GitHubParseCommand {

    private const val REPO_INFO_API = "https://api.github.com/repos"
    private const val CANVAS_WIDTH = 1620
    private const val CANVAS_HEIGHT = 1000
    private val githubLogo = ImageIO.read(Resources.loadFromResources("github/favicon.png"))
    private val starIcon = ImageIO.read(Resources.loadFromResources("github/star.png"))
    private val issueIcon = ImageIO.read(Resources.loadFromResources("github/issue.png"))
    private val forkIcon = ImageIO.read(Resources.loadFromResources("github/fork.png"))
    private val customFont = Font("Serif", Font.PLAIN, 50).deriveFont(Font.BOLD or Font.ITALIC)
    private val titleCustomFont = Font("Serif", Font.PLAIN, 75).deriveFont(Font.BOLD or Font.ITALIC)
    private val forkParentFont = Font("Serif", Font.PLAIN, 35).deriveFont(Font.ITALIC)
    private val descriptionCustomFont = Font("Serif", Font.PLAIN, 40).deriveFont(Font.BOLD or Font.ITALIC)
    private val backgroundColor = Color.WHITE
    private val textColor = Color(60, 60, 60)

    private val languageColors = mapOf(
        "Kotlin" to Color(169, 123, 255),
        "Java" to Color(176, 114, 25),
        "Python" to Color(53, 114, 165),
        "JavaScript" to Color(241, 224, 90),
        "C++" to Color(243, 75, 125),
        "Go" to Color(0, 173, 216),
        "Ruby" to Color(112, 21, 22),
        "PHP" to Color(79, 93, 149),
        "TypeScript" to Color(43, 116, 137),
        "Swift" to Color(240, 81, 56),
        "SCSS" to Color(198, 83, 140),
        "Visual Basic 6.0" to Color(44, 99, 83),
        "Astro" to Color(255, 90, 3),
        "Rust" to Color(222, 165, 132),
        "PowerShell" to Color(1, 36, 86),
        "MDX" to Color(252, 179, 44),
        "Shell" to Color(137, 224, 81),
        "CMake" to Color(218, 52, 52),
        "HTML" to Color(227, 76, 38),
        "Unknown" to Color(211, 211, 211)
    )

    private fun getLanguageColor(language: String): Color {
        return languageColors[language] ?: languageColors["Unknown"]!!
    }

    private fun getRepoStat(username: String, repo: String): RepoInfo {
        return Http.get<RepoInfo>(
            "$REPO_INFO_API/$username/$repo",
            headers = mapOf("Authorization" to "Bearer ${configManager.githubKey}")
        )
    }

    private fun createImage(repoStat: RepoInfo): String {
        val canvas = BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g2d = canvas.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.font = customFont
        g2d.color = backgroundColor
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
        g2d.color = this.getLanguageColor(repoStat.language)
        g2d.fillRect(0, CANVAS_HEIGHT - 20, CANVAS_WIDTH, 35)
        g2d.drawCustomImage(githubLogo, 1300, 740, 100.0, 100.0, false) // draw github logo
        g2d.drawCustomImage(starIcon, 80, 785, 70.0, 70.0, false) // draw star icon
        g2d.drawCustomImage(issueIcon, 480, 785, 70.0, 70.0, false)  // draw issue icon
        g2d.drawCustomImage(forkIcon, 880, 780, 70.0, 70.0, false)  // draw fork icon
        g2d.color = textColor
        g2d.drawString("Stars", 160, 840)
        g2d.drawString("Issues", 560, 840)
        g2d.drawString("Forks", 960, 840)
        g2d.drawString(repoStat.starsCount.formatNumberEnglish(), 200, 780)
        g2d.drawString(repoStat.openIssueCount.formatNumberEnglish(), 600, 780)
        g2d.drawString(repoStat.forksCount.formatNumberEnglish(), 1000, 780)
        if (repoStat.fork) {
            g2d.font = forkParentFont
            val parentName = repoStat.parent?.fullName!!
            val truncatedParentFullName = setTruncat("Forked from: $parentName", g2d, 1000)
            g2d.drawString(truncatedParentFullName, 80, 435)
        }
        g2d.font = titleCustomFont
        val truncatedFullNameText = setTruncat(repoStat.fullName, g2d, 1000)
        g2d.drawString(truncatedFullNameText, 80, 280)
        g2d.font = descriptionCustomFont
        g2d.drawString(repoStat.description ?: "暂无描述~", 80, 520, 800)
        val avatarImage = ImageIO.read(URI(repoStat.owner.avatarUrl).toURL())
        g2d.drawCustomImage(avatarImage, 1200, 160, 300.0, 300.0, true)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }

    suspend fun parse(listener: OBMessage, message: GroupMessage) {
        val path = message.rawMessage
            .replace(".git", "")
            .replace("https://github.com/", "")
            .replace("git@github.com:", "")
            .split("/")
        val user = path.first()
        val repo = path.last()
        val repoStat = this.getRepoStat(user, repo)
        val image = this.createImage(repoStat)
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addImage(image, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}