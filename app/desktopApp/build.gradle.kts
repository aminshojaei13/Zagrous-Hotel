import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(21)
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.app.shared)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.braveboy.hotelzagrous.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "HotelZagrous"
            packageVersion = "1.0.0"

            windows {
                packageVersion = "1.0.0"
                // UUID اختصاصی برای برنامه شما - برای آپدیت‌های بعدی این را تغییر ندهید
                upgradeUuid = "d7c8053a-1234-4321-abcd-1234567890ab"
                menu = true
                shortcut = true
                // در صورت داشتن آیکون، خط زیر را فعال کنید:
                // iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
