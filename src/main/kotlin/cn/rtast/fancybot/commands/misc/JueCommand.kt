/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/21
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.util.misc.Resources
import cn.rtast.fancybot.util.misc.drawCircularImage
import cn.rtast.fancybot.util.misc.makeGif
import cn.rtast.fancybot.util.misc.toBufferedImage
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import com.madgag.gif.fmsware.GifDecoder
import java.awt.image.BufferedImage
import java.net.URI

@CommandDescription("撅撅你")
class JueCommand : BaseCommand() {
    override val commandNames = listOf("撅")

    private val avatarUrl = "https://q1.qlogo.cn/g?b=qq&nk=#{}&s=640"

    private fun createJueGIF(source: String, target: String): String {
        val baseGIF = Resources.loadFromResources("misc/jue.gif")
        val sourceAvatar = URI(avatarUrl.replace("#{}", source)).toURL().readBytes().toBufferedImage()
        val targetAvatar = URI(avatarUrl.replace("#{}", target)).toURL().readBytes().toBufferedImage()
        val decoder = GifDecoder()
        decoder.read(baseGIF)
        val frames = mutableListOf<BufferedImage>()

        fun drawFrame(index: Int, sourceX: Int, sourceY: Int, targetX: Int, targetY: Int) {
            val frame = decoder.getFrame(index)
            val g2d = frame.createGraphics()
            g2d.drawCircularImage(sourceAvatar, sourceX, sourceY, 126.0, 126.0)
            g2d.drawCircularImage(targetAvatar, targetX, targetY, 126.0, 126.0)
            g2d.dispose()
            frames.add(frame)
        }

        (0 until decoder.frameCount).forEach {
            when (it) {
                0 -> drawFrame(it, 115, -12, 3, 175)
                1 -> drawFrame(it, 109, 2, 10, 170)
                2 -> drawFrame(it, 130, -13, 5, 154)
            }
        }
        val gifBytes = decoder.makeGif(frames)
        val gif = gifBytes.encodeToBase64()
        return gif
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val target = message.message.find { it.type == ArrayMessageType.at }?.data?.qq.toString()
        val source = message.sender.userId.toString()
        val gif = this.createJueGIF(source, target)
        val msg = MessageChain.Builder().addImage(gif, true).build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}