package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.getOrNull
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.json.JSONObject
import java.io.IOException

fun JSONObject.toLinearPage(pageSize: PDRectangle, children: MutableList<Element>): LinearPage {
    return LinearPage(
            dynamic = this.getOrNull<Boolean>("dynamic") ?: false,
            pageSize = pageSize,
            children = children
    )
}

class LinearPage(
        private val dynamic: Boolean = false,
        pageSize: PDRectangle,
        children: MutableList<Element> = mutableListOf()
) : Page(pageSize, children) {

    override fun preCalculate() = Unit

    override fun copy(): LinearPage {
        return LinearPage(
                pageSize = this.pageSize,
                children = this.children.map { it.copy() }.toMutableList()
        )
    }

    private fun createPageContentStream(): PDPageContentStream {
        return PDPage().apply { mediaBox = pageSize }
                .also { document?.addPage(it) }
                .let { PDPageContentStream(document, it) }
    }

    @Throws(IOException::class)
    override fun draw() {
        var content = createPageContentStream()
        var shiftY = 0f

        children.forEach { element ->
            if (element is RectangularElement) element.preCalculate()
            element.gravity = element.gravity and GravityType.GRAVITY_CENTER_HORIZONTAL or GravityType.GRAVITY_TOP

            if (element is RectangularElement && element.startY + shiftY < this.startY) {
                content = content.apply { close() }.let { createPageContentStream() }
                shiftY = 0f
            }

            element.shiftY = shiftY

            element.draw(content)

            if (element is RectangularElement) shiftY -= element.height - element.verticalPaddingCoefficient
        }

        content.close()
    }
}