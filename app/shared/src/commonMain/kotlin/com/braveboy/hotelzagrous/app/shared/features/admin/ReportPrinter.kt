@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.braveboy.hotelzagrous.app.shared.features.admin

expect object ReportPrinter {
    fun printHtml(html: String, jobName: String)
}
