package com.braveboy.hotelzagrous

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // تنظیمات پورت درخواستی کاربر:
    // 8091: پنل مدیریت (Admin)
    // 8090: پنل رزرو (Reservation)
    val urlParams = window.location.search
    val currentPort = window.location.port
    
    val isAdmin = currentPort == "8091" ||
                  urlParams.contains("mode=admin") || 
                  window.location.pathname.contains("admin")
    
    ComposeViewport {
        App(isAdmin = isAdmin)
    }
}
