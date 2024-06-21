plugins {
    jacoco
    id("checkstyle")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    id("application")
    id("antlr")
    id("org.openjfx.javafxplugin") version "0.1.0"
    kotlin("jvm")
    id("org.sonarqube") version "4.4.1.3373"
}

group = "org.gecko"
version = "0.1"

javafx {
    version = "21.0.1"
    modules("javafx.controls")
}

repositories {
    mavenCentral()
}



dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testfx:testfx-junit5:4.0.18")

    implementation("org.antlr:antlr4-runtime:4.13.1")
    antlr("org.antlr:antlr4:4.13.1")

    implementation("org.eclipse.elk:org.eclipse.elk.core:0.8.1")
    implementation("org.eclipse.elk:org.eclipse.elk.alg.common:0.8.1")
    implementation("org.eclipse.elk:org.eclipse.elk.alg.force:0.8.1")
    implementation("org.eclipse.elk:org.eclipse.elk.alg.layered:0.8.1")

    implementation("org.fxmisc.richtext:richtextfx:0.11.2")
    implementation("com.miglayout:miglayout-javafx:11.3")
    implementation("org.kordamp.ikonli:ikonli-materialdesign2-pack:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")

    implementation("no.tornado:tornadofx:1.7.20")

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.hildan.fxgson:fx-gson:5.0.0")

    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

checkstyle { toolVersion = "10.12.5" }

val generateGrammarSource by tasks.existing(AntlrTask::class) {
    arguments.add("-visitor")
    arguments.add("-package")
    arguments.add("gecko.parser")
}

tasks.getByName("compileJava").dependsOn(generateGrammarSource)

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

// Exclude the package from the coverage report
    val excludes = listOf("gecko/parser/*") // Add other packages if needed
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            excludes.forEach { ex ->
                exclude("**/$ex/**")
            }
        }
    }))
}

sonar {
    properties {
        property("sonar.projectKey", "wadoon_Gecko")
        property("sonar.organization", "wadoon")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

application { mainClass.set("org.gecko.application.Main") }

tasks {
    shadowJar {
        exclude("module-info.class")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.getByName("compileKotlin").dependsOn(generateGrammarSource)
tasks.getByName("compileTestKotlin").dependsOn(generateGrammarSource)
tasks.getByName("compileTestKotlin").dependsOn("generateTestGrammarSource")
