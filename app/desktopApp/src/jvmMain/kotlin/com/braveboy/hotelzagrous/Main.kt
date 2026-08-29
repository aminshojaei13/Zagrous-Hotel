package com.braveboy.hotelzagrous

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import hotelzagrous.app.shared.generated.resources.Res
import hotelzagrous.app.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() {
    // تنظیم رندرینگ نرم‌افزاری برای سازگاری حداکثری با ویندوز 7 و سیستم‌های قدیمی
    // اگر کارت گرافیک سیستم ضعیف باشد، این خط باعث می‌شود برنامه بدون مشکل باز شود
    System.setProperty("skiko.renderApi", "SOFTWARE")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Hotel Zagrous",
            icon = painterResource(Res.drawable.logo)
        ) {
            App(true)
        }
    }
}
