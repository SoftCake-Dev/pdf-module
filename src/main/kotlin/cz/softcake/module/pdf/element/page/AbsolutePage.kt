package cz.softcake.module.pdf.element.page

import cz.softcake.module.pdf.element.Element
import cz.softcake.module.pdf.element.RectangularElement
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.json.JSONObject
import java.io.IOException

fun JSONObject.toAbsolutePage(pageSize: PDRectangle, children: MutableList<Element>): AbsolutePage {
    return AbsolutePage(
            pageSize = pageSize,
            children = children
    )
}

class AbsolutePage(
        pageSize: PDRectangle,
        children: MutableList<Element> = mutableListOf()
) : Page(pageSize, children) {

    override fun preCalculate() {
        children.filterIsInstance<RectangularElement>()
                .forEach { it.preCalculate() }
    }

    override fun copy(): AbsolutePage {
        return AbsolutePage(
                pageSize = this.pageSize,
                children = this.children.map { it.copy() }.toMutableList()
        )
    }

    @Throws(IOException::class)
    override fun draw() {
        PDPage().apply { mediaBox = pageSize }
                .also { document?.addPage(it) }
                .let { PDPageContentStream(document, it) }
                .also { content -> children.forEach { it.draw(content) } }
                .apply { close() }
    }
}