/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.fancybot.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandDescription(val description: String)