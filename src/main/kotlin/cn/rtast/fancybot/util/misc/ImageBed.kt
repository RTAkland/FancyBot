/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/8
 */


package cn.rtast.fancybot.util.misc

import cn.rtast.fancybot.configManager
import cn.rtast.fancybot.entity.github.UploadContentPayload
import cn.rtast.fancybot.entity.github.UploadContentResponse
import cn.rtast.fancybot.enums.ImageBedType
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.proxy
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.rob.util.ob.OneBotAction
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.util.UUID

object ImageBed {

    private val imageBedUrl =
        "https://api.github.com/repos/${configManager.githubUser}/${configManager.githubImageRepo}/contents".proxy

    private val s3Client = this.createS3Client()

    private fun createS3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            configManager.cloudflareR2AccessKey,
            configManager.cloudflareR2SecretKey
        )
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create("https://${configManager.cloudflareAccountId}.r2.cloudflarestorage.com"))
            .build()
    }

    fun upload(file: ByteArray, fileType: String = ""): String {
        val randomUUID = UUID.randomUUID().toString()
        return when (configManager.imageBedType) {
            ImageBedType.Github -> {
                val body = UploadContentPayload("uploadImage", file.encodeToBase64()).toJson()
                val response = Http.put<UploadContentResponse>(
                    "$imageBedUrl/${randomUUID}${if (fileType.isNotBlank()) ".$fileType" else ""}", body,
                    mapOf("Authorization" to "Bearer ${configManager.githubKey}")
                ).content.name
                return "https://raw.githubusercontent.com/${configManager.githubUser}/${configManager.githubImageRepo}/refs/heads/main/$response".proxy
            }

            ImageBedType.CloudflareR2 -> {
                val ft = if (fileType.isNotBlank()) ".$fileType" else ""
                val key = "$randomUUID${ft}"
                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(configManager.cloudflareR2BucketName)
                    .contentType("image/${ft.replace(".", "")}")
                    .key(key)
                    .build()
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file))
                return "${configManager.cloudflareR2PublicUrl}/$key".proxy
            }
        }
    }
}