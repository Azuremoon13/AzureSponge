import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "xyz.azuremoon"
version = "1.1.0"

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:5.9.0")

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

// Fat-jar builder
val fatJar = tasks.register<Jar>("fatJar") {
    manifest {
        attributes.apply { put("Main-Class", "xyz.azuremoon.AzureSponge") }
    }
    archiveFileName.set("${rootProject.name}-${project.version}.jar")
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    duplicatesStrategy = DuplicatesStrategy.WARN
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

// Ensure fatJar and copyJar are run
tasks.getByName("build") {
    dependsOn(fatJar)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}