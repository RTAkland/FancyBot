import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

group = "cn.rtast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "repo.rtast.cn"
        url = uri("https://repo.rtast.cn/api/v4/projects/33/packages/maven")
    }
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