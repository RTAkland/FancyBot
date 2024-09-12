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