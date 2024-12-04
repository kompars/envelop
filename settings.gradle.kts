rootProject.name = "envelop"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("modules:core")
include("modules:kotlinx-html")
include("modules:mox")
include("modules:mox-ktor")
