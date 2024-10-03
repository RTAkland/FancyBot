/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/3
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.music.TopSong
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.MusicShareType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener

@CommandDescription("获取随机热榜音乐(网易云)")
class RandomMusicCommand : BaseCommand() {
    override val commandNames = listOf("随机音乐", "随机歌曲")

    private val ncmApiUrl = "${configManager.ncmAPI}/top/song"

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val id = Http.get<TopSong>(ncmApiUrl).data.random().privilege.id
        val msg = MessageChain.Builder()
            .addMusicShare(MusicShareType.Netease, id.toString())
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}