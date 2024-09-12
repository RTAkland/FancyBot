/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot.util

import cn.rtast.fancybot.util.str.fromJson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

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
            paramsUrl.dropLast(1)
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

    inline fun <reified T> executeRequest(request: Request): T {
        val result = okHttpClient.newCall(request).execute().body.string()
        return result.fromJson<T>()
    }

    private fun executeRequest(request: Request): String {
        return okHttpClient.newCall(request).execute().body.string()
    }

    fun executeRequestAsync(
        requestBuilder: Request.Builder,
        headers: Map<String, String>?,
        callback: (String) -> Unit
    ) {
        this.addHeaders(requestBuilder, headers)
        okHttpClient.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body.string()
                callback(responseBody)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(e.toString())
            }
        })
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

    @JvmOverloads
    inline fun <reified T> get(
        url: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null
    ): T {
        return get(url, params, headers).fromJson<T>()
    }

    @JvmOverloads
    inline fun <reified T> post(
        url: String,
        formBody: Map<String, String>? = null,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null
    ): T {
        val body = FormBody.Builder()
        val paramsUrl = buildParams(url, params)
        formBody?.let {
            it.forEach { (key, value) ->
                body.add(key, value)
            }
        }
        val request = Request.Builder()
            .post(body.build())
            .url(paramsUrl)
        val headerRequest = addHeaders(request, headers)
        return this.executeRequest<T>(headerRequest.build())
    }

    @JvmOverloads
    inline fun <reified T> post(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null
    ): T {
        val result = post(url, jsonBody, headers, params)
        return result.fromJson<T>()
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

    inline fun <reified T> getAsync(
        url: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
        noinline callback: (T?) -> Unit
    ) {
        val paramsUrl = buildParams(url, params)
        val request = Request.Builder()
            .url(paramsUrl)
            .get()

        executeRequestAsync(request, headers) { response ->
            try {
                callback(response.fromJson<T>())
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

    inline fun <reified T> postAsync(
        url: String,
        jsonBody: String,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null,
        noinline callback: (T?) -> Unit
    ) {
        val body = jsonBody.toRequestBody(jsonMediaType)
        val paramsUrl = buildParams(url, params)
        val request = Request.Builder()
            .post(body)
            .url(paramsUrl)
        this.addHeaders(request, jsonHeader)

        executeRequestAsync(request, headers) { response ->
            try {
                callback(response.fromJson<T>())
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}
