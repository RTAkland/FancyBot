/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/11/7
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.ROOT_PATH
import cn.rtast.rob.ROneBotFactory
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.entity.PrivateMessage
import cn.rtast.rob.interceptor.ExecutionInterceptor
import cn.rtast.rob.util.BaseCommand
import java.io.File

class CommandInterceptor : ExecutionInterceptor() {

    companion object {
        val file = File(ROOT_PATH, "/caches/command_execution_count").apply { createNewFile() }
    }

    override suspend fun afterGroupExecute(message: GroupMessage, command: BaseCommand) {
        file.writeText(ROneBotFactory.totalCommandExecutionTimes.toString())
    }

    override suspend fun afterPrivateExecute(message: PrivateMessage, command: BaseCommand) {
        file.writeText(ROneBotFactory.totalCommandExecutionTimes.toString())
    }
}