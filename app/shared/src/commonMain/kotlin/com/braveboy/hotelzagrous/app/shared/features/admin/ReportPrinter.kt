package com.braveboy.hotelzagrous.app.shared.features.admin

expect object ReportPrinter {
    fun printHtml(html: String, jobName: String)
}
