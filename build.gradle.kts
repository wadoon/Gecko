plugins {
    jacoco
    java
    application
    antlr
    kotlin("jvm")

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.sonarqube") version "5.0.0.4638"
    id("com.ncorti.ktfmt.gradle") version "0.18.0"
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
    implementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.4.3")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

//checkstyle { toolVersion = "10.12.5" }

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

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }
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


ktfmt {
    // Google style - 2 space indentation & automatically adds/removes trailing commas
    //googleStyle()

    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()

    // Breaks lines longer than maxWidth. Default 100.
    //maxWidth.set(80)
    // blockIndent is the indent size used when a new block is opened, in spaces.
    //blockIndent.set(8)
    // continuationIndent is the indent size used when a line is broken because it's too
    //continuationIndent.set(8)
    // Whether ktfmt should remove imports that are not used.
    //removeUnusedImports.set(false)
    // Whether ktfmt should automatically add/remove trailing commas.
    //manageTrailingCommas.set(false)
}
