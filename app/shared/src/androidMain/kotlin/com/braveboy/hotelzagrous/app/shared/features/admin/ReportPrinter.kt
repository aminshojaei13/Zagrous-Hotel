package com.braveboy.hotelzagrous.app.shared.features.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File

actual object ReportPrinter {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    actual fun printHtml(html: String, jobName: String) {
        val context = appContext ?: return
        
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    actual fun savePdf(html: String, fileName: String) {
        printHtml(html, fileName)
    }

    actual fun openInBrowser(html: String) {
        val context = appContext ?: return
        try {
            val tempFile = File(context.cacheDir, "report.html")
            tempFile.writeText(html)
            
            // Note: In a real app, you'd use FileProvider here to share the file safely.
            // For a simple report preview, we'll try to open it.
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(tempFile), "text/html")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
