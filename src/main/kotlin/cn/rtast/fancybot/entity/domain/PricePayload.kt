/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/17
 */


package cn.rtast.fancybot.entity.domain

import com.google.gson.annotations.SerializedName

data class PricePayload(
    @SerializedName("DomainList")
    val domainList: List<String>,
    @SerializedName("Filter")
    val filter: Int = 0,
    @SerializedName("Period")
    val period: Int = 1,
    @SerializedName("HashId")
    val hashId: String = "1726541001813_9",
    @SerializedName("SaveDomainSearch")
    val saveDomainSearch: Boolean = true,
    val serviceType: String = "domain",
    @SerializedName("Version")
    val version: String = "2018-08-08",
    @SerializedName("Action")
    val action: String = "BatchCheckDomain",
)