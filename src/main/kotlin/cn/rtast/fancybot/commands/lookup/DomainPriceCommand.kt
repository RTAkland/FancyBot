/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/17
 */


package cn.rtast.fancybot.commands.lookup

import cn.rtast.fancybot.entity.domain.PricePayload
import cn.rtast.fancybot.entity.domain.PriceResponse
import cn.rtast.fancybot.util.Http
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.str.toJson
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OBMessage
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage

class DomainPriceCommand : BaseCommand() {
    override val commandNames = listOf("/domain")

    private val canvasWidth = 300
    private val canvasHeight = 200
    private val backgroundColor = Color(255, 250, 200)
    private val secondLayerColor = Color(240, 255, 240)
    private val textColor = Color.BLACK
    private val domainApi = "https://dnspod.cloud.tencent.com"
    private val font = Font("Serif", Font.PLAIN, 13)

    private fun createCommonCanvas(): BufferedImage {
        val canvas = BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = canvas.createGraphics()
        g2d.color = backgroundColor
        g2d.fillRect(0, 0, canvasWidth, canvasHeight)
        g2d.color = secondLayerColor
        g2d.fillRect(20, 20, canvasWidth - 40, canvasHeight - 40)
        g2d.dispose()
        return canvas
    }

    private fun createDomainPriceCard(suffix: String, domainPrice: Pair<Int, Int>): String {
        val canvas = this.createCommonCanvas()
        val g2d = canvas.createGraphics()
        g2d.font = font
        g2d.color = textColor
        g2d.drawString("域名后缀: $suffix 首年注册价格: ${domainPrice.first}元", 50, 80)
        g2d.drawString("续费价格: ${domainPrice.second}元", 50, 100)
        g2d.drawString("数据来源: 腾讯云", 50, 120)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }

    private fun createRegisterCard(
        domain: String,
        available: Boolean,
        premium: Boolean,
        domainPrice: Pair<Int, Int>
    ): String {
        val canvas = this.createCommonCanvas()
        val g2d = canvas.createGraphics()
        g2d.font = font
        g2d.color = textColor
        g2d.drawString("域名$domain${if (available) "可以注册" else "已被注册"}", 50, 60)
        g2d.drawString("${if (premium) "是" else "不是"}白金域名", 50, 80)
        g2d.drawString("首年年注册价格: ${domainPrice.first}元", 50, 100)
        g2d.drawString("续费价格: ${domainPrice.second}元", 50, 120)
        g2d.drawString("数据来源: 腾讯云", 50, 140)
        g2d.dispose()
        return canvas.toByteArray().encodeToBase64()
    }


    override suspend fun executeGroup(listener: OBMessage, message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            val msg = MessageChain.Builder()
                .addAt(message.sender.userId)
                .addText("发送`/domain <域名>`即可查询对应的注册和续费价格~")
                .build()
            listener.sendGroupMessage(message.groupId, msg)
            return
        }
        val suffix = args.first()
        val domain = if (suffix.contains(".")) args.first() else "rtast.$suffix"
        val response = Http.post<PriceResponse>(
            "$domainApi/cgi/capi",
            PricePayload(listOf(domain)).toJson(),
            params = mapOf(
                "action" to "BatchCheckDomain",
                "from" to "domain_buy",
                "csrfCode" to "",
                "uin" to 0,
                "_" to "1726541082487",
                "notUseInnerMark" to 1
            )
        ).data.response.domainList.first()

        val cardImage =
            if (suffix.contains(".")) {
                this.createRegisterCard(
                    domain,
                    response.available,
                    response.premium,
                    response.realPrice to response.price
                )
            } else {
                this.createDomainPriceCard(suffix, response.realPrice to response.price)
            }
        val msg = MessageChain.Builder()
            .addAt(message.sender.userId)
            .addImage(cardImage, true)
            .build()
        listener.sendGroupMessage(message.groupId, msg)
    }
}