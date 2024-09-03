/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.fancybot.util.item

class ItemManager {

    val items = mutableListOf<Item>()

    fun register(item: Item) {
        items.add(item)
    }
}