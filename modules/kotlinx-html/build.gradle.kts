plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
    explicitApi()

    jvm()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.modules.core)
            api(libs.kotlinx.html)
        }
    }
}
