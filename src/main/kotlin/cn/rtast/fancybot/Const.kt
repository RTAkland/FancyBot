/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */


package cn.rtast.fancybot

import cn.rtast.fancybot.commands.AboutCommand
import cn.rtast.fancybot.commands.EchoCommand
import cn.rtast.fancybot.commands.HelpCommand
import cn.rtast.fancybot.commands.StatusCommand
import cn.rtast.fancybot.commands.lookup.*
import cn.rtast.fancybot.commands.misc.*
import cn.rtast.fancybot.commands.niuzi.*
import cn.rtast.fancybot.commands.parse.AsciiArtCommand
import cn.rtast.fancybot.commands.parse.ReverseGIFCommand
import cn.rtast.fancybot.commands.misc.JrrpCommand
import cn.rtast.fancybot.commands.niuzi.NiuziRedeemCommand
import cn.rtast.fancybot.commands.misc.ShortLinkCommand
import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.entity.enums.ImageType
import cn.rtast.fancybot.entity.enums.WSType
import cn.rtast.fancybot.items.BaisiItem
import cn.rtast.fancybot.items.HeisiItem
import cn.rtast.fancybot.items.SetuItem
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.fancybot.util.file.NiuziBankManager
import cn.rtast.fancybot.util.file.NiuziManager
import cn.rtast.fancybot.util.item.ItemManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

const val ROOT_PATH = "./data"

val DEFAULT_CONFIG = Config(
    ncmAPI = "https://ncm.rtast.cn",
    wsAddress = "ws://127.0.0.1",
    wsType = WSType.Client,
    accessToken = "114514",
    port = 6760,
    listeningGroups = listOf(114514, 1919810),
    qweatherKey = "114514",
    githubKey = "1919810",
    imageType = ImageType.PNG,
    openAIAPIHost = "https://api.moonshot.cn",
    openAIAPIKey = "114514",
    openAIModel = "moonshot-v1-8k",
    smtpHost = "127.0.0.1",
    smtpPort = 25,
    smtpUser = "114514",
    smtpPassword = "1919810",
    smtpFromAddress = "fancybot@repo.rtast.cn",
    admins = listOf(3458671395L),
    enableAntiRevoke = true,
    llamaUrl = "http://127.0.0.1",
    llamaModel = "llama3.1",
    qqMusicApiUrl = "http://127.0.0.1:3200"
)

val configManager = ConfigManager()
val itemManager = ItemManager()
val niuziManager = NiuziManager()
val niuziBankManager = NiuziBankManager()

val items = listOf(
    HeisiItem(),
    BaisiItem(),
    SetuItem()
)

val START_UP_TIME = Instant.now().epochSecond

val commands = listOf(
    EchoCommand(), JrrpCommand(),
    NiuziRedeemCommand(), HitokotoCommand(),
    FKXQSCommand(), QRCodeCommand(),
    AntiRevokeCommand(), MCPingCommand(),
    WeatherCommand(), CigaretteCommand(),
    RemakeCommand(), PixivCommand(),
    RUACommand(), NslookupCommand(),
    CompilerCommand(), AICommand(),
    GithubUserCommand(), AboutCommand(),
    MusicPlayUrlCommand(), DomainPriceCommand(),
    SendMailCommand(), ZiBiCommand(),
    UnsetZiBiCommand(), WikipediaCommand(),
    AsciiArtCommand(), StatusCommand(),
    JueCommand(), ShortLinkCommand(),
    TenSetuCommand(), ShotSelfCommand(),
    ShotOtherCommand(), ReverseGIFCommand(),
    HelpCommand(), MusicCommand(),
    LikeMeCommand(), NiuziTransferCommand(),
    NiuziSignCommand(), NiuziJiJianCommand(),
    NiuziQueryCommand(), MyNiuziCommand(),
    NiuziBankCommand(), WithdrawCommand(),
    BankTransferCommand(), CreateBankAccountCommand(),
    BankBalanceCommand(), DepositCommand(),
    DMSearchCommand(), LlamaCommand(),
    QQMusicCommand(), BullShitGenerateCommand()
)