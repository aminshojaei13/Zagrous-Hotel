package com.braveboy.hotelzagrous

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    // تنظیم رندرینگ نرم‌افزاری برای سازگاری حداکثری با ویندوز 7 و سیستم‌های قدیمی
    // اگر کارت گرافیک سیستم ضعیف باشد، این خط باعث می‌شود برنامه بدون مشکل باز شود
    System.setProperty("skiko.renderApi", "SOFTWARE")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Hotel Zagrous",
        ) {
            App(true)
        }
    }
}
