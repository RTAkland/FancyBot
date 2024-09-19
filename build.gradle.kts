import buildsrc.MCSMDeploy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

val fancyBotVersion: String by project

group = "cn.rtast"
version = fancyBotVersion

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.rtast.cn/api/v4/projects/33/packages/maven")
    maven("https://repo.rtast.cn/api/v4/projects/19/packages/maven")
}

tasks.compileKotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}


dependencies {
    implementation(libs.rOneBot)
    implementation(libs.okhttp)
    implementation(libs.exposedCore)
    implementation(libs.exposedJDBC)
    implementation(libs.sqliteJDBC)
    implementation(libs.motdPinger)
    implementation(libs.animated.gif.lib)
    implementation(libs.simple.java.mail)
    implementation(libs.jsoup)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "cn.rtast.fancybot.FancyBotKt",
            "Manifest-Version" to "1.0"
        )
    }
}

tasks.register("deployBot") {
    val apiKey = System.getenv("MCSM_API_KEY")
    val apiUrl = System.getenv("MCSM_API_URL")
    val daemonId = System.getenv("MCSM_DAEMON_ID")
    val instanceId = System.getenv("MCSM_INSTANCE_ID")
    MCSMDeploy(apiUrl, apiKey, daemonId, instanceId).deploy(file("build/libs/FancyBot-1.0.0-all.jar"))
}