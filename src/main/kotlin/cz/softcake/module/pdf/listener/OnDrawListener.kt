package cz.softcake.module.pdf.listener

import java.io.IOException
import org.apache.pdfbox.pdmodel.PDPageContentStream

interface OnDrawListener {
    @Throws(IOException::class)
    fun onDrawStarted(contentStream: PDPageContentStream): Unit? = null

    @Throws(IOException::class)
    fun onDraw(contentStream: PDPageContentStream): Unit? = null

    @Throws(IOException::class)
    fun onDrawFinished(contentStream: PDPageContentStream): Unit? = null
}