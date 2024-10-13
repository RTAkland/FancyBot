/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/13
 */


package cn.rtast.fancybot.entity.douyin

import com.google.gson.annotations.SerializedName

data class DouyinVideo(
    val data: Data
) {
    data class Data(
        @SerializedName("aweme_detail")
        val awemeDetail: AwemeDetail
    )

    data class AwemeDetail(
        val author: Author,
        val desc: String,
        val statistics: Statistics,
        val video: Video,
        @SerializedName("share_url")
        val shareUrl: String,
    )

    data class Author(
        val nickname: String,
        @SerializedName("follower_count")
        val followerCount: Int,
        val signature: String,
        @SerializedName("avatar_thumb")
        val avatarThumb: AvatarThumb,
    )

    data class AvatarThumb(
        @SerializedName("url_list")
        val urlList: List<String>,
    )

    data class Statistics(
        // 收藏数
        @SerializedName("collect_count")
        val collectCount: Int,
        // 评论数
        @SerializedName("comment_count")
        val commentCount: Int,
        // 点赞数
        @SerializedName("digg_count")
        val diggCount: Int,
        // 播放数
        @SerializedName("play_count")
        val playCount: Int,
        // 分享数
        @SerializedName("share_count")
        val shareCount: Int,
    )

    data class Video(
        @SerializedName("cover_original_scale")
        val coverOriginalScale: CoverOriginalScale,
        @SerializedName("bit_rate")
        val bitRate: List<BitRate>
    )

    data class BitRate(
        @SerializedName("play_addr")
        val playAddr: PlayAddr
    )

    data class PlayAddr(
        @SerializedName("url_list")
        val urlList: List<String>,
    )

    data class CoverOriginalScale(
        @SerializedName("url_list")
        val urlList: List<String>,
        val height: Int,
        val width: Int
    )
}