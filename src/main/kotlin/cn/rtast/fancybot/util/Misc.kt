/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/5
 */


package cn.rtast.fancybot.util

import kotlin.random.Random

fun randomBooleanWithProbability(probability: Double): Boolean {
    val randomValue = Random.nextInt(100)
    return randomValue <= (probability * 100)
}