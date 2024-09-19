/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.music.Search
import cn.rtast.fancybot.entity.music.SearchedResult
import cn.rtast.fancybot.entity.music.SongUrl
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.MusicShareType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.time.Instant

class MusicCommand : BaseCommand() {
    override val commandNames = listOf("/music", "/点歌", "点歌")

    private val searchedResult = mutableMapOf<Long, List<SearchedResult>>()

    companion object {
        fun searchSong(keyword: String): Search {
            return Http.get<Search>(
                "${configManager.ncmAPI}/search",
                mapOf("keywords" to keyword),
            )
        }
    }

    private suspend fun legacyMusicCommand(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`/点歌`或者`/music` <关键词> 来搜索音乐~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        searchedResult.forEach { (key, results) ->
            val validResults = results.filter { result ->
                Instant.now().epochSecond - result.timestamp <= 30
            }
            if (validResults.isEmpty()) {
                searchedResult.remove(key)
            } else {
                searchedResult[key] = validResults
            }
        }
        val method = args.first()
        when (method) {
            "p", "pp", "play", "播放" -> {
                try {
                    val index = args.drop(1).last().toInt()
                    val searchedResultGet = searchedResult[message.sender.userId]!!
                    val finalResult = searchedResultGet[index]
                    val msg = MessageChain.Builder()
                        .addMusicShare(MusicShareType.Netease, finalResult.id.toString())
                        .build()
                    listener.sendGroupMessage(message.groupId, msg)
                } catch (_: Exception) {
                    val msg = MessageChain.Builder()
                        .addAt(message.sender.userId)
                        .addText("输入有误请重新搜索本次搜索结果已清除")
                        .build()
                    listener.sendGroupMessage(message.groupId, msg)
                }
                searchedResult.remove(message.sender.userId)
            }

            else -> {
                if (searchedResult.containsKey(message.sender.userId)) {
                    searchedResult.remove(message.sender.userId)
                    val msg = MessageChain.Builder()
                        .addAt(message.sender.userId)
                        .addText("你上次的搜索结果还没用呢, 已经帮你把上次的搜索结果清除掉啦~")
                        .build()
                    listener.sendGroupMessage(message.groupId, msg)
                }
                val keyword = args.joinToString(" ")
                val result = searchSong(keyword)
                val tempResults = mutableListOf<SearchedResult>()
                result.result.songs.asSequence().take(5).forEach {
                    val artists = it.artists.joinToString(", ") { it.name }
                    tempResults.add(SearchedResult(it.id, it.name, artists, Instant.now().epochSecond))
                }
                searchedResult[message.sender.userId] = tempResults
                val stringResult = StringBuilder("搜索结果如下:\n")
                tempResults.forEachIndexed { index, searchedResult ->
                    stringResult.append("| $index | 《${searchedResult.name}》-- ${searchedResult.artists}\n")
                }
                stringResult.append("输入/music p <序号> 播放\n你有30秒的时间进行操作")
                val msg = MessageChain.Builder()
                    .addAt(message.sender.userId)
                    .addText(stringResult.toString())
                    .build()
                listener.sendGroupMessage(message.groupId, msg)
            }
        }
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.last() == "legacy" || args.last() == "l" || args.last() in "0".."4") {
            this.legacyMusicCommand(listener, message, args)
            return
        }
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`点歌 <歌名>`即可点第一首搜索到的歌曲~")
                .addNewLine()
                .addText("发送`点歌 <歌名> [l|legacy]`即可使用旧版点歌系统")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }

        val keyword = args.joinToString(" ")
        val result = searchSong(keyword).result.songs.asSequence().first().id
        val msg = MessageChain.Builder()
            .addMusicShare(MusicShareType.Netease, result.toString())
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}

class MusicPlayUrlCommand : BaseCommand() {
    override val commandNames = listOf("/lj", "lj", "song", "/song")

    private fun getPlayUrl(id: String): String {
        return Http.get<SongUrl>("${configManager.ncmAPI}/song/url", mapOf("id" to id)).data.first().url
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val url = if (args.last() == "id" || args.last() == "i") {
            val id = args.first()
            this.getPlayUrl(id)
        } else {
            val keyword = args.joinToString(" ")
            val id = MusicCommand.searchSong(keyword).result.songs.first().id.toString()
            this.getPlayUrl(id)
        }
        val msg = MessageChain.Builder()
            .addReply(message.messageId)
            .addText(url)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}