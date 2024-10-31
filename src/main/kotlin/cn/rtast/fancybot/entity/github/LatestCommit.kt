/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/3
 */


package cn.rtast.fancybot.entity.github

data class LatestCommit(
    val sha: String,
    val commit: Commit,
) {
    data class Commit(
        val committer: Committer,
    )

    data class Committer(
        val email: String,
        val name: String,
        val date: String,
    )
}