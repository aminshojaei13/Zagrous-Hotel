package com.braveboy.hotelzagrous.app.shared.features.admin

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLIFrameElement

actual object ReportPrinter {
    actual fun printHtml(html: String, jobName: String) {
        val iframe = document.createElement("iframe") as HTMLIFrameElement
        iframe.style.display = "none"
        document.body?.appendChild(iframe)
        
        val doc = iframe.contentWindow?.document
        doc?.open()
        doc?.write(html)
        doc?.close()

        iframe.contentWindow?.focus()
        iframe.contentWindow?.print()
        
        window.setTimeout({
            document.body?.removeChild(iframe)
        }, 1000)
    }

    actual fun savePdf(html: String, fileName: String) {
        printHtml(html, fileName)
    }

    actual fun openInBrowser(html: String) {
        val newWindow = window.open("", "_blank")
        newWindow?.document?.write(html)
        newWindow?.document?.close()
    }
}
