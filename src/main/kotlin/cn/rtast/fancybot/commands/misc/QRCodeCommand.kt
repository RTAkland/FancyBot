/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/30
 */


package cn.rtast.fancybot.commands.misc

import cn.rtast.fancybot.annotations.CommandDescription
import cn.rtast.fancybot.enums.CommandAction
import cn.rtast.fancybot.util.file.insertActionRecord
import cn.rtast.fancybot.util.str.encodeToBase64
import cn.rtast.fancybot.util.toByteArray
import cn.rtast.rob.entity.GroupMessage
import cn.rtast.rob.util.BaseCommand
import cn.rtast.rob.util.ob.MessageChain
import cn.rtast.rob.util.ob.OneBotListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter

@CommandDescription("生成二维码")
class QRCodeCommand : BaseCommand() {
    override val commandNames = listOf("/qr")

    private fun generateQRCode(content: String): String {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300    , hints)
        return MatrixToImageWriter.toBufferedImage(bitMatrix).toByteArray().encodeToBase64()
    }

    override suspend fun executeGroup(listener: OneBotListener, message: GroupMessage, args: List<String>) {
        val content = args.joinToString(" ")
        val image = this.generateQRCode(content)
        val msg = MessageChain.Builder()
            .addImage(image, true)
            .build()
        message.reply(msg)
        insertActionRecord(CommandAction.GenQRCode, message.sender.userId)
    }
}