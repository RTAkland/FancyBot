/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


package cn.rtast.fancybot.entity.mc

import com.google.gson.annotations.SerializedName

data class Skin(
    val properties: List<Property>,
) {
    data class Property(
        val value: String,
    )
}

data class DecodedSkin(
    val textures: Texture,
) {
    data class Texture(
        @SerializedName("SKIN")
        val skin: SKIN,
    )

    data class SKIN(
        val url: String,
    )
}