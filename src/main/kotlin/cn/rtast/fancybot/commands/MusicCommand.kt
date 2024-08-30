/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.music.Search
import cn.rtast.fancybot.entity.music.SearchedResult
import cn.rtast.fancybot.entity.music.SongUrl
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.OBMessage
import java.time.Instant

class MusicCommand : BaseCommand() {
    override val commandName = "/music"

    private val searchedResult = mutableMapOf<Long, List<SearchedResult>>()

    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
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
            "s", "ss", "搜索" -> {
                if (searchedResult.containsKey(message.sender.userId)) {
                    searchedResult.remove(message.sender.userId)
                    listener.sendGroupMessage(
                        message.groupId,
                        "你上次的搜索结果还没用呢, 已经帮你把上次的搜索结果清除掉啦~"
                    )
                }
                val keyword = args.drop(1).joinToString(" ")
                val result = Http.get<Search>(
                    "${configManager.ncmAPI}/search",
                    mapOf("keywords" to keyword),
                )
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
                listener.sendGroupMessage(message.groupId, stringResult.toString())
            }

            "p", "pp", "播放" -> {
                try {
                    val index = args.drop(1).last().toInt()
                    val searchedResultGet = searchedResult[message.sender.userId]!!
                    val finalResult = searchedResultGet[index]
                    val url =
                        Http.get<SongUrl>(
                            "${configManager.ncmAPI}/song/url",
                            mapOf("id" to finalResult.id)
                        ).data.first().url
                    val cqCode = "[CQ:music,type=custom,url=https://music.163.com/#/song?id=${finalResult.id}," +
                            "audio=$url,title=《${finalResult.name}》-- ${finalResult.artists}]"
                    listener.sendGroupMessage(message.groupId, cqCode)
                } catch (_: Exception) {
                    listener.sendGroupMessage(message.groupId, "输入有误请重新搜索本次搜索结果已清除")
                }
                searchedResult.remove(message.sender.userId)
            }
        }
    }
}