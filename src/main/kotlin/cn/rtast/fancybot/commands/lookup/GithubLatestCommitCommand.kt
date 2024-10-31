/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/3
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.commands.parse.GitHubParseCommand
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.github.LatestCommit
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.fromArrayJson
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain

@CommandDescription("查询Github上的仓库的最新一次的提交信息")
class GithubLatestCommitCommand : BaseCommand() {
    override val commandNames = listOf(".commit")

    private val userInfoUrl = "https://api.github.com/repos"

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`.commit <用户名/仓库名>`即可查询仓库最新一次的提交信息")
        }
        try {
            val (user, repo) = if (args.size == 1) {
                args.first().split("/").first() to args.first().split("/").last()
            } else {
                args.first() to args.last()
            }
            val response = Http.get(
                "$userInfoUrl/$user/$repo/commits",
                headers = mapOf("Authorization" to configManager.githubKey)
            ).fromArrayJson<List<LatestCommit>>().first()
            val image = GitHubParseCommand.creatRepoImage(user, repo)
            val msg = MessageChain.Builder()
                .addImage(image, true)
                .addText("最新提交ID: ${response.sha.substring(0, 7)}")
                .addNewLine()
                .addText("提交者: ${response.commit.committer.name}(${response.commit.committer.email})")
                .addNewLine()
                .addText("提交日期: ${response.commit.committer.date}")
                .addNewLine()
                .addText("https://github.com/$user/$repo")
                .build()
            message.reply(msg)
        } catch (e: Exception) {
            e.printStackTrace()
            message.reply("查询失败~")
        }
    }
}