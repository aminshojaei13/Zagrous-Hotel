plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ktor) apply false
}

tasks.register<Zip>("zipWebDistribution") {
    group = "release"
    description = "Builds and packages the Web (JS & Wasm) distribution into a ZIP file."

    archiveFileName.set("web-distribution.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/web"))

    // Add JS and WasmJs distributions to the zip
    from(project(":app:webApp").layout.buildDirectory.dir("distributions"))

    dependsOn(":app:webApp:jsBrowserDistribution")
    dependsOn(":app:webApp:wasmJsBrowserDistribution")
}

tasks.register("releaseAllExceptDesktop") {
    group = "release"
    description = "Builds release artifacts for Android, Web, and Server (excludes Desktop)."

    // Android release build
    dependsOn(":app:androidApp:assembleRelease")

    // Web distribution (via the zip task)
    dependsOn("zipWebDistribution")

    // Server distribution
    dependsOn(":server:installDist")
}
