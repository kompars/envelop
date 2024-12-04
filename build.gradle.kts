plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.git.versioning)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlinx.bcv)
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.maven.publish) apply false
}

group = "org.kompars.envelop"
version = "0.0.0-SNAPSHOT"

gitVersioning.apply {
    describeTagPattern = "v(?<version>[0-9]+\\.[0-9]+\\.[0-9]+)"
    refs.tag(describeTagPattern) {
        version = "\${ref.version}"
    }
    refs.branch(describeTagPattern.removePrefix("v")) {
        version = "\${ref.version}-SNAPSHOT"
    }
    rev {
        version = "\${describe.tag.version.major}.\${describe.tag.version.minor.next}.0-SNAPSHOT"
    }
}

repositories {
    mavenCentral()
}
