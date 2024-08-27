/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.entity.Search
import cn.rtast.fancybot.entity.SongUrl
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage

class MusicCommand : BaseCommand() {
    override val commandName = "/点歌"

    override fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        val keyword = args.joinToString(" ")
        Http.getAsync<Search>("https://music.api.yhdzz.cn/search?keywords=$keyword") {
            val name = it!!.result.songs.first().name
            val id = it.result.songs.first().id
            val artists = it.result.songs.first().artists.joinToString(", ") { it.name }
            Http.getAsync<SongUrl>("https://music.api.yhdzz.cn/song/url?id=$id") { songUrl ->
                listener.sendGroupMessage(
                    message.groupId,
                    "[CQ:music," +
                            "type=custom," +
                            "url=https://music.163.com/#/song?id=$id," +
                            "audio=${songUrl?.data?.first()?.url}," +
                            "title=《$name》-- $artists]\n"
                )
            }
        }
    }
}