/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/14
 */


package buildsrc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()


fun Any.toJson(): String {
    return gson.toJson(this)
}

inline fun <reified T> String.fromJson(): T {
    return gson.fromJson(this, T::class.java)
}

inline fun <reified T> String.fromArrayJson(): T {
    return gson.fromJson(this, object : TypeToken<T>() {}.type)
}
