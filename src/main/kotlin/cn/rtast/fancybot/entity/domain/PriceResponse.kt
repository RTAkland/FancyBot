/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/17
 */


package cn.rtast.fancybot.entity.domain

import com.google.gson.annotations.SerializedName

data class PriceResponse(
    val data: Data
) {
    data class Data(
        @SerializedName("Response")
        val response: Response
    )

    data class Response(
        @SerializedName("DomainList")
        val domainList: List<Domain>
    )

    data class Domain(
        @SerializedName("RealPrice")
        val realPrice: Int,
        @SerializedName("Price")
        val price: Int,
        @SerializedName("Premium")
        val premium: Boolean,
        @SerializedName("Available")
        val available: Boolean,
    )
}