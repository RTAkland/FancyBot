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
import cn.rtast.fancybot.util.str.setTruncate
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.ob.MessageChain
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
        "C#" to Color(23, 134, 0),
        "F#" to Color(184, 69, 252),
        "Scala" to Color(194, 45, 64),
        "CSS" to Color(86, 61, 124),
        "XML" to Color(0, 96, 172),
        "Dart" to Color(0, 180, 171),
        "Objective-C" to Color(67, 142, 255),
        "OCaml" to Color(239, 122, 8),
        "Jupyter Notebook" to Color(218, 91, 11),
        "Haskell" to Color(239, 122, 8),
        "GDScript" to Color(0x355570),
        "Elm" to Color(0x60b5cc),
        "Dockerfile" to Color(0x384d54),
        "C" to Color(0x555555),
        "Batchfile" to Color(0xC1, 0xF7, 0x65),
        "AngelScript" to Color(0xC7d7dc),
        "Vue" to Color(0x41b883),
        "SQF" to Color(0x3f3f3f),
        "R" to Color(0x198ce7),
        "Assembly" to Color(0x6e4c13),
        "M" to Color(0x9a6700),
        "HCL" to Color(0x844fba),
        "Scilab" to Color(0xca0f21),
        "TeX" to Color(0x3d6117),
        "Makefile" to Color(0x427819),
        "Svelte" to Color(0xff3e00),
        "Stylus" to Color(0xff6347),
        "EJS" to Color(0xa91e50),
        "PLpgSQL" to Color(0x336790),
        "Markdown" to Color(0x083fa1),
        "Lua" to Color(0x000080),
        "JSON" to Color(0x292929),
        "YAML" to Color(0xcb171e),
        "HLSL" to Color(0xaace60),
        "GLSL" to Color(0x5686a5),
        "Jinja" to Color(0xa52a22),
        "Zig" to Color(0xec915c),
        "Perl" to Color(0x0298c3),
        "Vim Script" to Color(0x199f4b),
        "Tcl" to Color(0xe4cc98),
        "Roff" to Color(0xecdebe),
        "CoffeeScript" to Color(0x244776),
        "Matlab" to Color(0xe16737),
        "Pascal" to Color(0xE3F171),
        "Fortran" to Color(0x4d41b1),
        "Prolog" to Color(0x74283c),
        "Erlang" to Color(0xB83998),
        "D" to Color(0xba595e),
        "ActionScript" to Color(0x882B0F),
        "Mathematica" to Color(0xdd1100),
        "Nim" to Color(0xffc200),
        "COBOL" to Color(0x9a6700),
        "Cuda" to Color(0x3a4e3a),
        "SAS" to Color(0xb34936),
        "Visual Basic .NET" to Color(0x945db7),
        "Less" to Color(0x1d365d),
        "Unknown" to Color(211, 211, 211)
    )

    private fun getLanguageColor(language: String?): Color {
        return languageColors[language] ?: languageColors["Unknown"]!!
    }

    private fun getRepoStat(username: String, repo: String): RepoInfo {
        return Http.get<RepoInfo>(
            "$REPO_INFO_API/$username/$repo", headers = mapOf("Authorization" to "Bearer ${configManager.githubKey}")
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
        g2d.drawString(repoStat.starsCount.formatNumberEnglish(), 180, 780)
        g2d.drawString(repoStat.openIssueCount.formatNumberEnglish(), 600, 780)
        g2d.drawString(repoStat.forksCount.formatNumberEnglish(), 1000, 780)
        if (repoStat.fork) {
            g2d.font = forkParentFont
            val parentName = repoStat.parent?.fullName!!
            val truncatedParentFullName = setTruncate("复刻自 $parentName", g2d, 1000)
            g2d.drawString(truncatedParentFullName, 80, 335)
        }
        g2d.font = titleCustomFont
        val truncatedFullNameText = setTruncate(repoStat.fullName, g2d, 1000)
        g2d.drawString(truncatedFullNameText, 80, 280)
        g2d.font = descriptionCustomFont
        g2d.drawString(repoStat.description ?: "暂无描述~", 80, 400, 800)
        val avatarImage = ImageIO.read(URI(repoStat.owner.avatarUrl).toURL())
        g2d.drawCustomImage(avatarImage, 1200, 160, 300.0, 300.0, true)
        g2d.color = this.getLanguageColor(repoStat.language)
        g2d.drawString(repoStat.language ?: "[无语言]", 30, CANVAS_HEIGHT - 50)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }

    suspend fun parse(message: GroupMessage, user: String, repo: String) {
        val repoStat = this.getRepoStat(user, repo)
        val image = this.createImage(repoStat)
        val msg = MessageChain.Builder().addImage(image, true).build()
        message.reply(msg)
    }

    fun creatRepoImage(user: String, repo: String): String {
        val repoStat = this.getRepoStat(user, repo)
        val image = this.createImage(repoStat)
        return image
    }
}