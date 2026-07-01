package com.braveboy.hotelzagrous.app.shared.features.admin

actual object ReportPrinter {
    actual fun printHtml(html: String, jobName: String) {
        // Basic implementation for Desktop, could use java.awt.Desktop or a library
        println("Printing $jobName: $html")
    }
}
