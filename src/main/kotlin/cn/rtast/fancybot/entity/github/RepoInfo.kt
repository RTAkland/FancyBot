/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/12
 */


package cn.rtast.fancybot.entity.github

import com.google.gson.annotations.SerializedName

data class RepoInfo(
    val owner: Owner,
    @SerializedName("full_name")
    val fullName: String,
    val description: String,
    @SerializedName("forks_count")
    val forksCount: Int,
    val language: String,
    @SerializedName("stargazers_count")
    val starsCount: Int,
    @SerializedName("open_issues_count")
    val openIssueCount: Int
) {
    data class Owner(
        @SerializedName("avatar_url")
        val avatarUrl: String,
    )
}