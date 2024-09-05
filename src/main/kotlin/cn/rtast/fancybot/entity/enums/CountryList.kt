/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.entity.enums

enum class CountryList(val chineseName: String) {
    China("中国"), UnitedStates("美国"), Russia("俄罗斯"),
    Germany("德国"), France("法国"), UnitedKingdom("英国"),
    Japan("日本"), Brazil("巴西"), Canada("加拿大"),
    Australia("澳大利亚"), India("印度"), Italy("意大利"),
    Spain("西班牙"), SouthKorea("韩国"), Mexico("墨西哥"),
    Indonesia("印度尼西亚"), Turkey("土耳其"), SouthAfrica("南非"),
    Argentina("阿根廷"), Netherlands("荷兰");

    companion object {
        fun getRandomCountry(): CountryList {
            return entries.random()
        }
    }
}