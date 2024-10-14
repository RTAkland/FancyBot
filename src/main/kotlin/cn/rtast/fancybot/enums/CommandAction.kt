/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


package cn.rtast.fancybot.enums

enum class CommandAction(val action: String) {
    SendMail("发送邮件"), Echo("使用Echo"), GenQRCode("生成二维码"),
    Status("获取状态"), Compiler("执行语言代码"), AntiRevoke("防撤回"),
    Wiki("查询Wiki"), AI("问AI"), Pastebin("生成pastebin")
}