/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.IPGeo
import cn.rtast.fancybot.util.Http
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.onebot.MessageChain
import cn.rtast.rob.onebot.toNode
import cn.rtast.rob.util.BaseCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Hashtable
import javax.naming.directory.InitialDirContext

@CommandDescription("解析域名获取IP地址")
class NslookupCommand : BaseCommand() {
    override val commandNames = listOf("/ns")

    companion object {
        private const val MC_SRV_PREFIX = "_minecraft._tcp"
        private const val IP_INFO_API = "https://ipinfo.io/###/json"
        private val env = Hashtable<String, String>().also {
            it["java.naming.factory.initial"] = "com.sun.jndi.dns.DnsContextFactory"
        }
        private val dirContext = InitialDirContext(env)

        fun srv(host: String): MutableList<String> {
            val srvDomain = "$MC_SRV_PREFIX.$host"
            val attrs = dirContext.getAttributes(srvDomain, arrayOf("SRV"))
            val srvRecords = mutableListOf<String>()
            val srvAttr = attrs.get("SRV")
            if (srvAttr != null) {
                for (i in 0 until srvAttr.size()) {
                    val srvRecord = srvAttr.get(i) as String
                    srvRecords.add(srvRecord)
                }
            }
            return srvRecords
        }
    }

    private fun geo(ip: String) = Http.get<IPGeo>(IP_INFO_API.replace("###", ip))

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val host = args.first()
        val nsType = if (args.size == 3) args[1] else "common"
        val srvType = if (args.size == 3) args.last() else "mc"
        try {
            if (nsType == "srv") {
                when (srvType) {
                    "mc" -> {
                        val srvRecords = srv(host)
                        val messages = mutableListOf<MessageChain>()
                        srvRecords.forEach {
                            val srv = it.split(" ")
                            val priority = srv.first().toInt()
                            val weight = srv[1].toInt()
                            val port = srv[2].toInt()
                            val host = srv.last()
                            val ip = InetAddress.getByName(host).hostAddress
                            val geo = this.geo(ip)
                            val msg = MessageChain.Builder()
                                .addText("解析后的地址: $host:$port")
                                .addNewLine()
                                .addText("权重: $weight | 优先级: $priority")
                                .addNewLine()
                                .addText("$host -> $ip")
                                .addNewLine()
                                .addText("国家: ${if (geo.country == "HK") "CN" else geo.country} 地区: ${geo.region} 城市: ${geo.city} | 所属组织: ${geo.org}")
                                .build()
                            messages.add(msg)
                        }
                        message.reply(messages.toNode(configManager.selfId))
                    }
                }
            } else {
                val ip = withContext(Dispatchers.IO) { InetAddress.getByName(host) }.hostAddress
                val geo = this.geo(ip)
                val msg = MessageChain.Builder()
                    .addText("$host -> $ip")
                    .addNewLine()
                    .addText("国家: ${geo.country} 地区: ${geo.region} 城市: ${geo.city} | 所属组织: ${geo.org}")
                    .build()
                message.reply(msg)
            }
        } catch (_: UnknownHostException) {
            message.reply("未知的主机名: $host")
        } catch (e: Exception) {
            message.reply("未知错误: ${e.message}")
        }
    }
}