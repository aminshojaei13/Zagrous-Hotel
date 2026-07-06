import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.gradle.api.tasks.bundling.Zip

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.shared)
            implementation(libs.compose.ui)
        }
    }
}

tasks.register<Zip>("zipWebNoGC") {
    dependsOn("jsBrowserDistribution")
    
    // فایل‌های اصلی وب
    from(layout.buildDirectory.dir("dist/js/productionExecutable"))
    
    // اضافه کردن فایل bat برای اجرای راحت در ویندوز
    from(projectDir) {
        include("run-web.bat")
    }

    archiveFileName.set("no-gc.zip")
    destinationDirectory.set(layout.projectDirectory)
}