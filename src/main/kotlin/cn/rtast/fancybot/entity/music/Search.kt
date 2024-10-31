/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.entity.music

data class Search(
    val result: Result,
) {
    data class Result(
        val songs: List<Song>,
    )

    data class Song(
        val id: Long,
        val name: String,
        val artists: List<Artist>,
    )

    data class Artist(
        val name: String,
    )
}