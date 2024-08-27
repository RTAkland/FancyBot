import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.0"
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
    implementation("cn.rtast:ROneBot:0.2.0")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "cn.rtast.fancybot.FancyBotKt",
            "Manifest-Version" to "1.0"
        )
    }
}