package cz.softcake.module.pdf.listener

import cz.softcake.module.pdf.model.Element
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.io.IOException

interface OnChildrenDrawListener {
    @Throws(IOException::class)
    fun onChildrenDrawStarted(contentStream: PDPageContentStream, children: List<Element>): Unit? = null

    @Throws(IOException::class)
    fun onChildrenDraw(contentStream: PDPageContentStream, children: List<Element>): Unit? = null

    @Throws(IOException::class)
    fun onChildrenDrawFinished(contentStream: PDPageContentStream, children: List<Element>): Unit? = null
}