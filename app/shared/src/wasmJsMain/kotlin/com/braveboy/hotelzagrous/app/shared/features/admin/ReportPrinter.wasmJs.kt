package com.braveboy.hotelzagrous.app.shared.features.admin

import kotlin.js.ExperimentalWasmJsInterop

actual object ReportPrinter {
    @OptIn(ExperimentalWasmJsInterop::class)
    actual fun printHtml(html: String, jobName: String) {
        printHtmlInternal(html)
    }
}

@ExperimentalWasmJsInterop
@JsFun("(html) => { const iframe = document.createElement('iframe'); iframe.style.display = 'none'; document.body.appendChild(iframe); const doc = iframe.contentWindow.document; doc.open(); doc.write(html); doc.close(); iframe.contentWindow.focus(); iframe.contentWindow.print(); setTimeout(() => { document.body.removeChild(iframe); }, 1000); }")
private external fun printHtmlInternal(html: String)
