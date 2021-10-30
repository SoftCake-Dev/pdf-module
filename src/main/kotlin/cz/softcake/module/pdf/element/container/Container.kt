package cz.softcake.module.pdf.element.container

import cz.softcake.module.pdf.element.Element
import cz.softcake.module.pdf.element.RectangularElement
import cz.softcake.module.pdf.element.RectangularElementGetters
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import kotlin.math.abs

interface ParentGetters : RectangularElementGetters {
    val document: PDDocument? get() = null
}

object SizeType {
    const val FILL_PARENT = -1f
    const val WRAP_CONTENT = -2f
}

abstract class Container(
        val children: MutableList<Element> = ArrayList(),
        height: Float = SizeType.FILL_PARENT,
        width: Float = SizeType.FILL_PARENT,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        gravity: Int = 0,
        id: String? = null
) : RectangularElement(
        height,
        width,
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        gravity,
        id
), ParentGetters {

    override val document: PDDocument?
        get() = this.parent?.document

    private val elements: MutableMap<String, Element> = children.stream()
            .filter { it.id != null && !it.id.startsWith("$") }
            .collect(Collectors.toMap({ it.id }) { it })

    init {
        children.forEach { it.parent = this }
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

    open fun onPreCalculateChildren() {
        children.filterIsInstance<RectangularElement>()
                .forEach { it.preCalculate() }
    }

    open fun onPreCalculateWrapContent() {
        if (height == SizeType.WRAP_CONTENT) {
            height = children.stream()
                    .filter { it is RectangularElement }
                    .map { it as RectangularElement }
                    .mapToDouble { (it.height + abs(it.verticalPaddingCoefficient)).toDouble() }
                    .max().orElse(this.height.toDouble()).toFloat()
        }

        if (width == SizeType.WRAP_CONTENT) {
            width = children.stream()
                    .filter { it is RectangularElement }
                    .map { it as RectangularElement }
                    .mapToDouble { (it.width + abs(it.horizontalPaddingCoefficient)).toDouble() }
                    .max().orElse(this.width.toDouble()).toFloat()
        }
    }

    override fun preCalculate() {
        this.onPreCalculateChildren()
        this.onPreCalculateWrapContent()
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

    @Throws(IOException::class)
    open fun onChildrenDrawStarted(contentStream: PDPageContentStream, children: List<Element>): Unit? = null

    @Throws(IOException::class)
    open fun onChildrenDraw(contentStream: PDPageContentStream, children: List<Element>) {
        for (element in children) {
            element.draw(contentStream)
        }
    }

    @Throws(IOException::class)
    open fun onChildrenDrawFinished(contentStream: PDPageContentStream, children: List<Element>): Unit? = null

    @Throws(IOException::class)
    override fun draw(contentStream: PDPageContentStream) {
        onChildrenDrawStarted(contentStream, children)
        onChildrenDraw(contentStream, children)
        onChildrenDrawFinished(contentStream, children)
        super.draw(contentStream)
    }
}