/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/1
 */


package cn.rtast.fancybot.entity.stock

data class StockSymbolQuery(
    val count: Int,
    val result: List<Result>
) {
    data class Result(
        val description: String,
        val displaySymbol: String,
        val symbol: String,
    )
}