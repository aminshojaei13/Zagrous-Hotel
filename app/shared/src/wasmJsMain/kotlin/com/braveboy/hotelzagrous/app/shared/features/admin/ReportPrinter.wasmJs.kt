package com.braveboy.hotelzagrous.app.shared.features.admin

import kotlin.js.ExperimentalWasmJsInterop

actual object ReportPrinter {
    @OptIn(ExperimentalWasmJsInterop::class)
    actual fun printHtml(html: String, jobName: String) {
        printHtmlInternal(html)
    }

    actual fun savePdf(html: String, fileName: String) {
        printHtml(html, fileName)
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    actual fun openInBrowser(html: String) {
        openInBrowserInternal(html)
    }
}

@ExperimentalWasmJsInterop
@JsFun("(html) => { const iframe = document.createElement('iframe'); iframe.style.display = 'none'; document.body.appendChild(iframe); const doc = iframe.contentWindow.document; doc.open(); doc.write(html); doc.close(); iframe.contentWindow.focus(); iframe.contentWindow.print(); setTimeout(() => { document.body.removeChild(iframe); }, 1000); }")
private external fun printHtmlInternal(html: String)

@ExperimentalWasmJsInterop
@JsFun("(html) => { const newWindow = window.open('', '_blank'); if (newWindow) { newWindow.document.write(html); newWindow.document.close(); } }")
private external fun openInBrowserInternal(html: String)
