/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/18
 */


package cn.rtast.fancybot.util.misc

import org.xbill.DNS.Lookup
import org.xbill.DNS.SRVRecord
import org.xbill.DNS.Type

fun String.resolveMinecraftSrv(): Pair<String, Int> {
    val srvDomain = "_minecraft._tcp.$this"
    val lookup = Lookup(srvDomain, Type.SRV)
    val result = lookup.run()
    if (result != null && result.isNotEmpty()) {
        val srvRecord = result[0] as SRVRecord
        val target = srvRecord.target.toString().trimEnd('.')
        val port = srvRecord.port
        return Pair(target, port)
    }
    return Pair(this, 25565)
}
