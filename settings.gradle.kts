rootProject.name = "envelop"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("modules:common")
include("modules:core")
include("modules:kotlinx-html")
include("modules:mox")
include("modules:mox-ktor")
include("modules:parser")
