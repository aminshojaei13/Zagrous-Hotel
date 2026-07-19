package com.braveboy.hotelzagrous.app.shared.features.admin

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.awt.Desktop
import java.awt.print.PrinterException
import java.io.File
import java.io.FileOutputStream
import java.text.MessageFormat
import javax.swing.JEditorPane
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.html.HTMLEditorKit

actual object ReportPrinter {
    actual fun printHtml(html: String, jobName: String) {
        SwingUtilities.invokeLater {
            try {
                val editorPane = JEditorPane().apply {
                    contentType = "text/html"
                    editorKit = HTMLEditorKit()
                    text = html
                    isEditable = false
                }

                // Set a reasonable size for layout calculation
                editorPane.setSize(800, 600)

                val header = MessageFormat(jobName)
                val footer = MessageFormat("Page {0}")

                // This opens the system print dialog. 
                // Users can choose a physical printer or "Print to PDF" if available on their OS.
                val success = editorPane.print(header, footer, true, null, null, true)
                
                if (success) {
                    println("Print job '$jobName' sent to printer.")
                } else {
                    println("Print job '$jobName' was cancelled.")
                }
            } catch (e: PrinterException) {
                System.err.println("Printer error: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                System.err.println("Error during printing: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    actual fun savePdf(html: String, fileName: String) {
        SwingUtilities.invokeLater {
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Save Report as PDF"
                selectedFile = File("$fileName.pdf")
                fileFilter = FileNameExtensionFilter("PDF Documents", "pdf")
            }

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                val file = fileChooser.selectedFile
                try {
                    FileOutputStream(file).use { os ->
                        val builder = PdfRendererBuilder()
                        builder.useFastMode()
                        builder.withHtmlContent(html, null)
                        builder.toStream(os)
                        builder.run()
                    }
                    println("PDF saved to: ${file.absolutePath}")
                } catch (e: Exception) {
                    System.err.println("Error saving PDF: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    actual fun openInBrowser(html: String) {
        try {
            val tempFile = File.createTempFile("report_", ".html")
            tempFile.writeText(html)
            tempFile.deleteOnExit()
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(tempFile.toURI())
            } else {
                // Fallback for some Linux environments
                Runtime.getRuntime().exec("xdg-open ${tempFile.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
