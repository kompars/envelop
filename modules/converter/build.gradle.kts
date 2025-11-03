plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.maven.publish)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
    explicitApi()

    compilerOptions {
        optIn = listOf("kotlin.time.ExperimentalTime")
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.modules.core)
            implementation(libs.simplejavamail)
            implementation(libs.ktor.http)
        }
    }
}
