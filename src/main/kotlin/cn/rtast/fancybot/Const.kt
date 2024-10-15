/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/8/27
 */

@file:Suppress("DEPRECATION")

package cn.rtast.fancybot

import cn.rtast.fancybot.commands.AboutCommand
import cn.rtast.fancybot.commands.EchoCommand
import cn.rtast.fancybot.commands.HelpCommand
import cn.rtast.fancybot.commands.StatusCommand
import cn.rtast.fancybot.commands.SupportMeCommand
import cn.rtast.fancybot.commands.lookup.*
import cn.rtast.fancybot.commands.misc.*
import cn.rtast.fancybot.commands.niuzi.*
import cn.rtast.fancybot.commands.parse.AsciiArtCommand
import cn.rtast.fancybot.commands.parse.ReverseGIFCommand
import cn.rtast.fancybot.commands.misc.JrrpCommand
import cn.rtast.fancybot.commands.niuzi.NiuziRedeemCommand
import cn.rtast.fancybot.commands.misc.ShortLinkCommand
import cn.rtast.fancybot.entity.Config
import cn.rtast.fancybot.enums.ImageBedType
import cn.rtast.fancybot.enums.ImageType
import cn.rtast.fancybot.enums.WSType
import cn.rtast.fancybot.items.*
import cn.rtast.fancybot.util.file.BlackListManager
import cn.rtast.fancybot.util.file.ConfigManager
import cn.rtast.fancybot.util.file.NiuziBankManager
import cn.rtast.fancybot.util.file.NiuziManager
import cn.rtast.fancybot.util.file.RCONManager
import cn.rtast.fancybot.util.item.ItemManager
import cn.rtast.rob.ROneBotFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

const val ROOT_PATH = "./data"
const val ASSETS_BASE_URL = "https://static.rtast.cn"
const val API_RTAST_URL = "https://api.rtast.cn"
const val PBI_API_URL = "https://pbi.us.kg"

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
    qqMusicApiUrl = "http://127.0.0.1:3200",
    noticeUser = 114514L,
    selfId = 114514L,
    tianXingApiKey = "1145141919810",
    githubUser = "RTAkland",
    githubImageRepo = "Static-Images",
    imageBedType = ImageBedType.Github,
    cloudflareAccountId = "111",
    cloudflareR2AccessKeyId = "11111",
    cloudflareR2SecretKey = "222",
    cloudflareR2BucketName = "fancybot",
    cloudflareR2PublicUrl = "https://114514.com",
    apiSpaceKey = "114514",
    apiRtastKey = "114514",
    azureAppClientId = "114514",
    youtubeDataApiKey = "114514"
)

val configManager = ConfigManager()
val itemManager = ItemManager()
val niuziManager = NiuziManager()
val niuziBankManager = NiuziBankManager()
val blackListManager = BlackListManager()
val rconManager = RCONManager()

val START_UP_TIME = Instant.now().epochSecond

val items = listOf(
    HeisiItem(), BaisiItem(),
    SetuItem(), NiuziItem(),
    TenSetuItem(), TenSetuR18Item()
)

val tasks = mapOf(
    10000000L to suspend {
        configManager.admins.forEach {
            ROneBotFactory.action.sendLike(it, 1)
        }
    }
)

val commands = listOf(
    EchoCommand(), JrrpCommand(), NiuziRedeemCommand(), HitokotoCommand(),
    KFCCommand(), GenerateQRCodeCommand(), AntiRevokeCommand(), MCPingCommand(),
    WeatherCommand(), CigaretteCommand(), RemakeCommand(), PixivCommand(),
    RUACommand(), NslookupCommand(), CompilerCommand(), AICommand(),
    GithubUserCommand(), AboutCommand(), MusicPlayUrlCommand(), DomainPriceCommand(),
    SendMailCommand(), ZiBiCommand(), UnsetZiBiCommand(), WikipediaCommand(),
    AsciiArtCommand(), StatusCommand(), JueCommand(), ShortLinkCommand(),
    ShotOtherCommand(), ReverseGIFCommand(), HelpCommand(), MusicCommand(),
    SendLikeCommand(), NiuziTransferCommand(), NiuziSignCommand(), NiuziJiJianCommand(),
    NiuziQueryCommand(), MyNiuziCommand(), NiuziBankCommand(), WithdrawCommand(),
    BankTransferCommand(), CreateBankAccountCommand(), BankBalanceCommand(), DepositCommand(),
    AnimeSearchCommand(), LlamaCommand(), QQMusicCommand(), BullShitGenerateCommand(),
    NiuziRankCommand(), NiuziBankRankCommand(), ShotSelfCommand(), TenSetuCommand(),
    ReactionCommand(), MCSkinCommand(), GithubLatestCommitCommand(), RandomMusicCommand(),
    ZDJDCommand(), ScanQRCodeCommand(), TTSCommand(), SlimeChunkHelperCommand(),
    SupportMeCommand(), TheCatCommand(), TheHistoryOfTodayCommand(), MCVersionCommand(),
    MinecraftWikiCommand(), GaoKaoDaysRemainCommand(), IdiomExplainCommand(), NiuziManagerCommand(),
    TodayEatCommand(), TodayDrinkCommand(), MCLoginCommand(), RCONCommand(),
    GravatarCommand(), PastebinCommand()
)