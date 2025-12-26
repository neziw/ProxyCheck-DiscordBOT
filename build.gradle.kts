import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("checkstyle")
}

group = "ovh.neziw"
version = "1.0.1"

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation")
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name} ${project.version}.jar")
    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "org/checkerframework/**",
        "META-INF/**",
        "javax/**"
    )
    manifest {
        attributes.set("Main-Class", "ovh.neziw.bot.BotBootstrap")
    }
    mergeServiceFiles()
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

checkstyle {
    toolVersion = "10.23.0"
    maxWarnings = 0
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    implementation("net.dv8tion:JDA:5.5.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("redis.clients:jedis:6.0.0")
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.8")
    implementation("ch.qos.logback:logback-classic:1.5.23")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}