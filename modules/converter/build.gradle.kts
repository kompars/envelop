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

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.modules.core)
            implementation(libs.kotlinx.datetime)
            implementation("org.simplejavamail:simple-java-mail:8.12.6")
        }
    }
}
