/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/4
 */


package cn.rtast.fancybot.entity.cigarette

import com.google.gson.annotations.SerializedName

data class Cigarette(
    @SerializedName("totalnum")
    val totalCount: Int,
    @SerializedName("productlist")
    val productList: List<Product>
) {
    data class Product(
        @SerializedName("productname")
        val productName: String,
        @SerializedName("coverpic_thumb400x300")
        val cover: String,
        val type: CigaretteType,
        val tar: Float,
        val nicotine: Float
    )

    data class CigaretteType(
        @SerializedName("typename")
        val typeName: String
    )
}