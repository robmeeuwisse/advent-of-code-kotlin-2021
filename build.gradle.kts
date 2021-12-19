plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        test {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.ExperimentalStdlibApi"
    }
}

dependencies {
    implementation("junit:junit:4.13")
    implementation(compose.desktop.currentOs)
}
