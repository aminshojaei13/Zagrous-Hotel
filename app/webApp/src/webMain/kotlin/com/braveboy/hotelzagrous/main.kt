package com.braveboy.hotelzagrous

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // تشخیص پورت برای نمایش صفحه مربوطه
    // اگر پورت 8081 بود صفحه رزرو، در غیر این صورت صفحه ادمین
    val isAdmin = window.location.port == "8081"
    
    ComposeViewport {
        App(isAdmin = isAdmin)
    }
}
