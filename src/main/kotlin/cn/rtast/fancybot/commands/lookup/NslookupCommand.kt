/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/6
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.configManager
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import cn.rtast.rob.util.ob.asNode
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
        private val env = Hashtable<String, String>().also {
            it["java.naming.factory.initial"] = "com.sun.jndi.dns.DnsContextFactory"
        }
        private val dirContext = InitialDirContext(env)
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val host = args.first()
        val nsType = args[1]
        val srvType = args.last()
        try {
            if (nsType == "srv") {
                when (srvType) {
                    "mc" -> {
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
                        val messages = mutableListOf<MessageChain>()
                        srvRecords.forEach {
                            val srv = it.split(" ")
                            val priority = srv.first().toInt()
                            val weight = srv[1].toInt()
                            val port = srv[2].toInt()
                            val host = srv.last()
                            val msg = MessageChain.Builder()
                                .addText("解析后的地址: $host:$port")
                                .addNewLine()
                                .addText("权重: $weight | 优先级: $priority")
                                .build()
                            messages.add(msg)
                        }
                        message.reply(messages.asNode(configManager.selfId))
                    }
                }
            } else {
                val msg = MessageChain.Builder()
                    .addText("${host}解析后的IP地址为: ")
                    .addText(withContext(Dispatchers.IO) { InetAddress.getByName(host) }.hostAddress)
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