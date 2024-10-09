/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.util.misc

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.github.UploadContentPayload
import cn.rtast.fancybot.entity.github.UploadContentResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.fancybot.util.str.toJson
import java.util.UUID

object ImageBed {

    private val imageBedUrl =
        "https://api.github.com/repos/${configManager.githubUser}/${configManager.githubImageRepo}/contents".proxy

    fun upload(file: ByteArray, fileType: String = ""): String {
        val body = UploadContentPayload("uploadImage", file.encodeToBase64()).toJson()
        val response = Http.put<UploadContentResponse>(
            "$imageBedUrl/${UUID.randomUUID()}${if (fileType.isNotBlank()) ".$fileType" else ""}", body,
            mapOf("Authorization" to "Bearer ${configManager.githubKey}")
        ).content.name
        return "https://raw.githubusercontent.com/${configManager.githubUser}/${configManager.githubImageRepo}/refs/heads/main/$response".proxy
    }
}