package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.cast
import cz.softcake.module.pdf.extensions.getOrNull
import cz.softcake.module.pdf.extensions.getOrThrow
import cz.softcake.module.pdf.extensions.toPageSize
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.stream.Collectors

fun JSONObject.toPage(pageType: String? = null): Page {
    val pageSize = (this.getOrNull<String>("pageType") ?: pageType).toPageSize()
    val children = this.getOrNull<JSONArray>("element")?.map { it.cast<JSONObject>().toElement() }?.toMutableList()
            ?: this.getOrNull<JSONObject>("element")?.toElement()?.let { mutableListOf(it) }
            ?: mutableListOf()

    return when(this.getOrThrow<String>("type")) {
        "absolutePage" -> this.toAbsolutePage(pageSize, children)
        "linearPage" -> this.toLinearPage(pageSize, children)
        else -> this.toAbsolutePage(pageSize, children)
    }
}

abstract class Page(
        protected val pageSize: PDRectangle,
        protected val children: MutableList<Element> = mutableListOf()
) : ParentGetters {

    var parent: Pdf? = null

    override val document: PDDocument?
        get() = parent?.document

    override val height: Float
        get() = pageSize.height

    override val width: Float
        get() = pageSize.width

    private val elements: MutableMap<String, Element> = children.stream()
            .filter { it.id != null && !it.id.startsWith("$") }
            .collect(Collectors.toMap({ it.id }) { it })

    init {
        children.forEach { it.parent = this }
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

    fun findElementById(id: String): Element? {
        return if (elements.containsKey(id)) {
            elements[id]
        } else {
            children.filterIsInstance<Container>()
                    .mapNotNull { it.findElementById(id) }
                    .firstOrNull()
        }
    }

    abstract fun preCalculate()
    abstract fun copy(): Page

    @Throws(IOException::class)
    abstract fun draw()
}