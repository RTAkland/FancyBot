/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/7
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

@CommandDescription("帮助你寻找指定空置域大小并且在某个区块范围内的最多史莱姆区块范围")
class SlimeChunkHelperCommand : BaseCommand() {
    override val commandNames = listOf("/slime")

    private fun isSlimeChunk(worldSeed: Long, chunkX: Int, chunkZ: Int): Boolean {
        val rng = Random(
            worldSeed + (chunkX * chunkX) * 0x4C1906 +
                    (chunkX * 0x5AC0DB) + (chunkZ * chunkZ) * 0x4307A7L +
                    (chunkZ * 0x5F24F) xor 0x3AD8025FL
        )
        return rng.nextInt(10) == 0
    }

    private fun countSlimeChunksInRegion(xStart: Int, zStart: Int, chunkSize: Int, worldSeed: Long): Int {
        var count = 0
        for (x in xStart until xStart + chunkSize) {
            for (z in zStart until zStart + chunkSize) {
                if (isSlimeChunk(worldSeed, x, z)) {
                    count++
                }
            }
        }
        return count
    }

    private fun findMaxSlimeChunks(
        worldSeed: Long,
        searchRange: Int,
        chunkSize: Int
    ): Pair<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int> {
        var maxCount = 0
        var bestStartLocation = Pair(0, 0)
        var bestEndLocation = Pair(0, 0)
        for (x in -searchRange until searchRange - chunkSize + 1) {
            for (z in -searchRange until searchRange - chunkSize + 1) {
                val count = countSlimeChunksInRegion(x, z, chunkSize, worldSeed)
                if (count > maxCount) {
                    maxCount = count
                    bestStartLocation = Pair(x, z)
                    bestEndLocation = Pair(x + chunkSize - 1, z + chunkSize - 1)
                }
            }
        }
        return Pair(Pair(bestStartLocation, bestEndLocation), maxCount)
    }

    private fun generateSlimeChunkImage(startX: Int, startZ: Int, endX: Int, endZ: Int, worldSeed: Long): String {
        val width = endX - startX + 1
        val height = endZ - startZ + 1
        val pixelSize = 15
        val imageWidth = width * pixelSize
        val imageHeight = height * pixelSize
        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics
        val borderColor = Color.LIGHT_GRAY
        for (x in startX..endX) {
            for (z in startZ..endZ) {
                val isSlime = isSlimeChunk(worldSeed, x, z)
                val fillColor = if (isSlime) Color.PINK else Color.WHITE
                val px = (x - startX) * pixelSize
                val pz = (z - startZ) * pixelSize
                graphics.color = fillColor
                graphics.fillRect(px, pz, pixelSize, pixelSize)
                graphics.color = borderColor
                graphics.drawRect(px, pz, pixelSize, pixelSize)
            }
        }
        graphics.dispose()
        return image.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addText("此指令可以借助一个种子来帮助查找最适合史莱姆农场的位置")
                .addNewLine()
                .addText("如果不指定史莱姆农场的宽度的话默认为17x17区块大小")
                .addNewLine()
                .addText("不指定搜索区块范围默认为X: 500~-500, Y: 500~-500, 共计搜索100万个区块")
                .addNewLine()
                .addText("还可以指定空置域的大小, 不指定默认为17x17")
                .addNewLine()
                .addText("你可以使用`/slime <种子> [范围(例如600)] [空置域大小]`")
                .build()
            message.reply(msg)
            return
        }
        val seed = args.first().toLong()
        val slimeFarmSize = if (args.size == 1) 17 else args[1].toInt()
        val range = if (args.size == 1) 500 else args[2].toInt()
        val (location, slimeCount) = findMaxSlimeChunks(seed, range, slimeFarmSize)
        val startX = location.first.first
        val startZ = location.first.second
        val endX = location.second.first
        val endZ = location.second.second
        val image = generateSlimeChunkImage(startX, startZ, endX, endZ, seed)
        val msg = MessageChain.Builder()
            .addText("起始区块坐标X:$startX Y:$startZ")
            .addNewLine()
            .addText("结束区块坐标X:$endX Y:$endZ")
            .addNewLine()
            .addText("共计史莱姆区块数量: $slimeCount")
            .addImage(image, true)
            .build()
        message.reply(msg)
    }
}