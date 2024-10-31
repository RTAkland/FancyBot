/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.fancybot.util.misc.toBufferedImage
import cn.rtast.fancybot.util.misc.toByteArray
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.enums.ArrayMessageType
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.image.BufferedImage
import java.net.URI

@CommandDescription("生成二维码")
class GenerateQRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/qr")

    private fun generateQRCode(content: String): String {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints)
        return MatrixToImageWriter.toBufferedImage(bitMatrix).toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (args.isEmpty()) {
            message.reply("发送`/qr <内容>`即可生成一张二维码的图片")
            return
        }
        val content = args.joinToString(" ")
        val image = this.generateQRCode(content)
        val msg = MessageChain.Builder()
            .addImage(image, true)
            .build()
        message.reply(msg)
        insertActionRecord(CommandAction.GenQRCode, message.sender.userId)
    }
}

@CommandDescription("解析二维码中的内容!")
class ScanQRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/scan")

    companion object {

        private val waitingList = mutableListOf<Long>()

        private fun scanQRCode(image: BufferedImage): String {
            val luminanceSource = BufferedImageLuminanceSource(image)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))
            val result = MultiFormatReader().decode(binaryBitmap)
            return result.text
        }

        suspend fun callback(message: GroupMessage) {
            if (message.sender() in waitingList) {
                waitingList.remove(message.sender())
                if (!message.message.any { it.type == ArrayMessageType.image }) {
                    message.reply("回复错误已取消本次操作")
                    return
                }
                val image = message.message.find { it.type == ArrayMessageType.image }?.data?.file!!
                val bufferedImage = URI(image).toURL().readBytes().toBufferedImage()
                val result = this.scanQRCode(bufferedImage)
                message.reply("二维码扫描结果: $result")
            }
        }
    }

    override suspend fun executeGroup(message: GroupMessage, args: List<String>) {
        if (message.sender() !in waitingList) {
            waitingList.add(message.sender())
            message.reply("继续发送一张二维码图片")
        } else {
            message.reply("回复错误已经取消本次操作")
        }
    }
}