package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.stream.Collectors

fun JSONObject.toPage(pageType: String? = null): Page {
    return Page(
            pageSize = (this.getOrNull<String>("type") ?: pageType).toPageSize(),
            children = this.getOrNull<JSONArray>("element")?.map { it.cast<JSONObject>().toElement() }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("element")?.toElement()?.let { mutableListOf(it) }
                    ?: mutableListOf()
    )
}

class Page(
        private val children: MutableList<Element> = mutableListOf(),
        private val pageSize: PDRectangle
) : RectangularElementGetters {

    private val page: PDPage = PDPage()

    override val height: Float
        get() = page.cropBox.height

    override val width: Float
        get() = page.cropBox.width

    private val elements: MutableMap<String, Element> = children.stream()
            .filter { it.id != null && !it.id.startsWith("$") }
            .collect(Collectors.toMap({ it.id }) { it })

    init {
        page.mediaBox = pageSize
        children.forEach {
            it.parent = this
            if (it is Container) {
                it.preCalculate()
            }
        }
    }

    fun findElementById(id: String): Element? {
        return if (elements.containsKey(id)) {
            elements[id]
        } else {
            children.filterIsInstance<Container>()
                    .mapNotNull { it.findElementById(id) }
                    .firstOrNull()
        }
    }

    fun addChild(child: Element) {
        child.parent = this
        this.children.add(child)
        if (child.id != null && !child.id.startsWith("$")) {
            elements[child.id] = child
        }
    }

    fun addAllChildren(children: List<Element>) {
        children.forEach { addChild(it) }
    }

    fun copy(): Page {
        return Page(
                pageSize = this.pageSize,
                children = children.map { it.copy() }.toMutableList()
        )
    }

    @Throws(IOException::class)
    fun draw(document: PDDocument) {
        document.addPage(page)

        PDPageContentStream(document, page).also { content ->
            children.forEach { it.draw(content) }
        }.apply { close() }
    }
}