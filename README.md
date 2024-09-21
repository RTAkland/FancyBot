<div align="center">

<h2>FancyBot 基于ROneBot的机器人😝</h2>

<h3>Made By <a href="https://github.com/RTAkland">RTAkland</a></h3>

<img src="https://static.rtast.cn/static/kotlin/made-with-kotlin.svg" alt="MadeWithKotlin">

<br>
<img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/RTAkland/FancyBot/main.yml">
<img alt="Kotlin Version" src="https://img.shields.io/badge/Kotlin-2.0.10-pink?logo=kotlin">
<img alt="GitHub" src="https://img.shields.io/github/license/RTAkland/FancyBot?logo=apache">

</div>

# 概述

> 这是一个使用了 `ROneBot` 框架的QQ机器人, 包含了20多种命令, 例如: 点歌, 今日人品, 签到...  
> 你可以在[这里](src/main/kotlin/cn/rtast/fancybot/commands/)来查看所有的命令

> ROneBot项目地址: [Gitlab](https://repo.rtast.cn/RTAkland/ronebot) [Github](https://github.com/RTAkland/ROneBot)

# 构建
由于项目使用的依赖过多项目构建出的产物在28Mb左右并且项目开启了
Github Action来自动化部署机器人, github Action的作用仅用作判断
构建是否成功以及自动部署机器人, 并不带有上传构建产物的功能(占用的空间太多)
所以你需要自行构建出机器人的jar包来运行

```shell
$ chmod +x ./gradlew
$ ./gradlew build clean
```

> 产物在`build/libs/`目录下你可以找到`-all`结尾的jar包

# 一些想说的话

OneBot协议本身并不是一个很好的协议, 在Kritor、Laana等新型协议完善后机器人会全面迁移到上述协议中
并且将OneBot版本的分支放置到另外一个分支中不再更新, 后续将主要更新Kritor/Laana的分支

# 开源

- 本项目以[Apache-2.0](./LICENSE)许可开源, 即:
    - 你可以直接使用该项目提供的功能, 无需任何授权
    - 你可以在**注明来源版权信息**的情况下对源代码进行任意分发和修改以及衍生

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FRTAkland%2FYeeeesMOTD.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FRTAkland%2FYeeeesMOTD?ref=badge_large)

# 鸣谢

<div>

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" alt="JetBrainsIcon" width="128">

<a href="https://www.jetbrains.com/opensource/"><code>JetBrains Open Source</code></a> 提供的强大IDE支持

</div>