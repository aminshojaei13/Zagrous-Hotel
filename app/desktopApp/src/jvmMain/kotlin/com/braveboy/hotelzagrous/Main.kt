package com.braveboy.hotelzagrous

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Hotel Zagrous",
    ) {
        App(true)
    }
}
