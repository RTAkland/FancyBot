/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/14
 */


package buildsrc

import java.io.File

class MCSMDeploy(
    private val mcsmApiUrl: String,
    private val apiKey: String,
    private val daemonId: String,
    private val instanceId: String
) {

    private fun deleteLastTimeJar() {
        Http.delete(
            "$mcsmApiUrl/api/files",
            DeleteTarget(listOf("/FancyBot-1.0.0-all.jar")).toJson(),
            mapOf(
                "daemonId" to daemonId,
                "uuid" to instanceId,
                "apikey" to apiKey
            )
        )
    }

    private fun getUploadConfig(): Pair<String, String> {
        val result = Http.post(
            "$mcsmApiUrl/api/files/upload?apikey=$apiKey&upload_dir=/&daemonId=$daemonId&uuid=$instanceId", ""
        )
        val config = result.fromJson<UploadConfig>().data
        return config.password to config.addr
    }

    private fun uploadFile(file: File, password: String, addr: String) {
        Http.postFile("${addr.replace("wss", "https")}/upload/$password", file)
    }

    private fun restartInstance() {
        Http.get("$mcsmApiUrl/api/protected_instance/restart?apikey=$apiKey&daemonId=$daemonId&uuid=$instanceId")
    }

    fun deploy(file: File) {
        this.deleteLastTimeJar()
        val config = this.getUploadConfig()
        this.uploadFile(file, config.first, config.second)
        this.restartInstance()
    }

    data class DeleteTarget(
        val targets: List<String>
    )

    data class UploadConfig(
        val data: Data
    ) {
        data class Data(
            val password: String,
            val addr: String
        )
    }
}