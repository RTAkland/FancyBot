/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/4
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.osu.OSUAccessToken
import cn.rtast.fancybot.entity.osu.OSUUserData
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.util.BaseCommand
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage

@CommandDescription("osu!")
class OSUCommand : BaseCommand() {
    override val commandNames = listOf("/osu")

    private val clientSecret = configManager.osuClientSecret
    private val clientId = configManager.osuClientId

    private fun getAccessToken(): String {
        return Http.post<OSUAccessToken>(
            "https://osu.ppy.sh/oauth/token",
            formBody = mapOf(
                "client_id" to clientId, "client_secret" to clientSecret,
                "grant_type" to "client_credentials", "scope" to "public"
            ),
            headers = mapOf("Content-Type" to "application/x-www-form-urlencoded", "Accept" to "application/json")
        ).accessToken
    }

    private fun getUser(username: String): OSUUserData {
        val token = this.getAccessToken()
        val resp = Http.get<OSUUserData>(
            "https://osu.ppy.sh/api/v2/users/$username/osu",
            headers = mapOf("Authorization" to "Bearer $token"),
        )
        return resp
    }

    private fun generateProgressBar(percent: Int, length: Int = 10): String {
        val completedLength = (percent * length) / 100
        val remainingLength = length - completedLength
        val progressBar = "█".repeat(completedLength) + " ".repeat(remainingLength)
        return "[$progressBar] $percent%"
    }

    private fun formatDuration(seconds: Long): String {
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val parts = mutableListOf<String>()
        if (days > 0) parts.add("${days}d")
        if (hours > 0) parts.add("${hours}h")
        if (minutes > 0) parts.add("${minutes}m")
        return parts.joinToString(" ")
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val username = args.first()
        val userData = getUser(username)
        val msg = MessageChain.Builder()
            .addText(userData.username)
            .addNewLine()
            .addText("等级 | Lv.${userData.statistics.level.current} ${generateProgressBar(userData.statistics.level.progress)}")
            .addNewLine()
            .addText("排名 | #${userData.statistics.globalRank} / ${userData.countryCode} #${userData.statistics.countryRank}")
            .addNewLine()
            .addText("准确率 | ${userData.statistics.hitAccuracy}%")
            .addNewLine()
            .addText("游玩时间 | ${formatDuration(userData.statistics.playTime)}")
            .addNewLine()
            .addText("游玩次数 | ${userData.statistics.playCount}")
            .build()
        message.reply(msg)
    }
}