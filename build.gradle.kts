import buildsrc.MCSMDeploy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    id("application")
}

val fancyBotVersion: String by project

group = "cn.rtast"
version = fancyBotVersion

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/main/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.rtast.cn/api/v4/projects/33/packages/maven")
    maven("https://repo.rtast.cn/api/v4/projects/19/packages/maven")
    maven("https://repo.rtast.cn/api/v4/projects/3/packages/maven")
}

tasks.compileKotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}


dependencies {
    implementation(libs.ronebot)
    implementation(libs.okhttp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.sqlite.jdbc)
    implementation(libs.mysql.jdbc)
    implementation(libs.postgressql.jdbc)
    implementation(libs.motd.pinger)
    implementation(libs.animated.gif.lib)
    implementation(libs.simple.java.mail)
    implementation(libs.jsoup)
    implementation(libs.nashorn.core)
    implementation(libs.zxing.core)
    implementation(libs.zxing.javase)
    implementation(libs.logback.classic)
    implementation(libs.slf4j.api)
    implementation(libs.aws.s3.sdk)
    implementation(libs.rconlib)
    implementation(libs.mcprotocollib)
    implementation("dnsjava:dnsjava:3.6.2")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    enabled = false
}

tasks.register("deployBot") {
    val apiKey = System.getenv("MCSM_API_KEY")
    val apiUrl = System.getenv("MCSM_API_URL")
    val daemonId = System.getenv("MCSM_DAEMON_ID")
    val instanceId = System.getenv("MCSM_INSTANCE_ID")
    MCSMDeploy(apiUrl, apiKey, daemonId, instanceId).deploy(file("build/libs/FancyBot-1.0.0-all.jar"))
}

application {
    mainClass = "cn.rtast.fancybot.FancyBotKt"
    applicationName = "FancyBot"
}