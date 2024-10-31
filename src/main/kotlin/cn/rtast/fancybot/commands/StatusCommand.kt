/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/20
 */


package cn.rtast.fancybot.commands

import cn.rtast.fancybot.START_UP_TIME
import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import java.time.Instant

@CommandDescription("状态")
class StatusCommand : BaseCommand() {
    override val commandNames = listOf("/status")

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val allocatedMemory = runtime.totalMemory() / (1024 * 1024)
        val freeMemory = runtime.freeMemory() / (1024 * 1024)
        val usedMemory = allocatedMemory - freeMemory
        val seconds = Instant.now().epochSecond - START_UP_TIME
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        val osName = System.getProperty("os.name") ?: "未知操作系统"
        val osArch = System.getProperty("os.arch") ?: "未知架构"
        val javaVersion = System.getProperty("java.version") ?: "未知JDK版本"
        val kotlinVersion = KotlinVersion.CURRENT.toString()
        val msg = MessageChain.Builder()
            .addText("内存占用: $usedMemory/${maxMemory}MB")
            .addNewLine()
            .addText("运行时间: ${days}天 ${hours}小时 ${minutes}分钟 ${remainingSeconds}秒")
            .addNewLine()
            .addText("操作系统: $osName / 系统架构: $osArch")
            .addNewLine()
            .addText("Java版本: $javaVersion / Kotlin版本: $kotlinVersion")
            .build()
        message.reply(msg)
        insertActionRecord(CommandAction.Status, message.sender.userId)
    }
}