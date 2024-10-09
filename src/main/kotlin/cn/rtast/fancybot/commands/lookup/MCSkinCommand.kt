/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/2
 */


@file:Suppress("KDocUnresolvedReference")

package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.entity.mc.DecodedSkin
import cn.rtast.fancybot.entity.mc.Skin
import cn.rtast.fancybot.entity.mc.Username
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.misc.isFullyTransparent
import cn.rtast.fancybot.util.misc.scaleImage
import cn.rtast.fancybot.util.str.decodeToString
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.fromJson
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

@CommandDescription("通过游戏名称或UUID来获取他的皮肤")
class MCSkinCommand : BaseCommand() {
    override val commandNames = listOf("/skin")

    private val uuidQueryUrl = "https://api.mojang.com/users/profiles/minecraft"
    private val skinUrl = "https://sessionserver.mojang.com/session/minecraft/profile"

    private fun String.isUsername(): Boolean {
        val usernamePattern = Regex("^[a-zA-Z][a-zA-Z0-9_]{2,15}$")
        return usernamePattern.matches(this)
    }

    /**
     * 截取整张皮肤中的头部部分, 如果是单层皮肤则直接截取脸部
     * 如果是双层皮肤也就将脸部作为背景图层, 将头发绘制在脸部上
     * 代码来自
     * `[YeeeesMOTD]
     * (https://github.com/RTAkland/YeeeesMOTD/blob/main/core/src/main/kotlin/cn/rtast/yeeeesmotd/utils/SkinHeadUtil.kt)`
     */
    private fun getSkinHead(skinUrl: String): String {
        val url = URI(skinUrl).toURL()
        val image = ImageIO.read(url)
        var subImage = image.getSubimage(8, 8, 8, 8)

        if (subImage.isFullyTransparent()) {
            subImage = image.getSubimage(40, 8, 8, 8)
        } else {
            val hairLayer = image.getSubimage(40, 8, 8, 8)
            val combined = BufferedImage(subImage.width, subImage.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = combined.createGraphics()
            g2d.drawImage(subImage, 0, 0, null)
            g2d.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
            g2d.drawImage(hairLayer, 0, 0, null)
            g2d.dispose()
            subImage = combined
        }
        val zoom = subImage.scaleImage(128 to 128)
        return zoom.toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/skin <uuid/游戏名称>`即可查询他的皮肤~ (仅限Java版)")
            return
        }
        try {
            val nameOrUUID = args.first().trim()
            val uuid = if (nameOrUUID.isUsername()) Http.get<Username>("$uuidQueryUrl/$nameOrUUID").id
            else nameOrUUID
            val skinUrl = Http.get<Skin>("$skinUrl/$uuid")
                .properties.first().value.decodeToString().fromJson<DecodedSkin>()
                .textures.skin.url
            val headBase64 = this.getSkinHead(skinUrl)
            val msg = MessageChain.Builder()
                .addImage(headBase64, true)
                .addText(skinUrl)
                .build()
            message.reply(msg)
        } catch (_: Exception) {
            message.reply("输入错误, 检查一下是否输入正确吧~")
        }
    }
}