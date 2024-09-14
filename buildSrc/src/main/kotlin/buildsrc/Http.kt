/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/14
 */


package buildsrc

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


object Http {

    val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    val jsonHeader = mapOf(
        "Content-Type" to "application/json; charset=utf-8",
        "Accept" to "application/json"
    )
    val okHttpClient = OkHttpClient()

    fun buildParams(url: String, params: Map<String, Any>?): String {
        val paramsUrl = StringBuilder("$url?")
        params?.let {
            it.forEach { (key, value) ->
                paramsUrl.append("$key=$value&")
            }
            paramsUrl.setLength(paramsUrl.length - 1)
        }
        return if (params != null) paramsUrl.toString() else url
    }

    fun addHeaders(request: Request.Builder, headers: Map<String, String>?): Request.Builder {
        headers?.let {
            it.forEach { (key, value) ->
                request.addHeader(key, value)
            }
        }
        return request
    }

    @JvmOverloads
    fun delete(
        url: String,
        jsonBody: String? = null,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null
    ): String {
        val paramsUrl = buildParams(url, params)
        val requestBuilder = Request.Builder().url(paramsUrl)
        jsonBody?.let {
            val body = it.toRequestBody(jsonMediaType)
            requestBuilder.delete(body)
        } ?: requestBuilder.delete()

        val headerRequest = addHeaders(requestBuilder, headers)
        return executeRequest(headerRequest.build())
    }

    @JvmOverloads
    fun postFile(
        url: String,
        file: File,
        formBody: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null
    ): String {
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        formBody?.forEach { (key, value) ->
            multipartBody.addFormDataPart(key, value.toString())
        }
        val fileMediaType = "application/octet-stream".toMediaType()
        multipartBody.addFormDataPart("file", file.name, file.asRequestBody(fileMediaType))

        val paramsUrl = buildParams(url, params)
        val request = Request.Builder()
            .url(paramsUrl)
            .post(multipartBody.build())
        val headerRequest = addHeaders(request, headers)
        return executeRequest(headerRequest.build())
    }

    private fun executeRequest(request: Request): String {
        return okHttpClient.newCall(request).execute().body.string()
    }

    @JvmOverloads
    fun post(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null
    ): String {
        val body = jsonBody.toRequestBody(jsonMediaType)
        val paramsUrl = buildParams(url, params)
        val request = Request.Builder()
            .post(body)
            .url(paramsUrl)
        this.addHeaders(request, jsonHeader)
        val headerRequest = addHeaders(request, headers)
        return this.executeRequest(headerRequest.build())
    }

    @JvmOverloads
    fun get(
        url: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null
    ): String {
        val paramsUrl = buildParams(url, params)
        val request = Request.Builder()
            .url(paramsUrl)
            .get()
        val headerRequest = addHeaders(request, headers)
        return this.executeRequest(headerRequest.build())
    }
}