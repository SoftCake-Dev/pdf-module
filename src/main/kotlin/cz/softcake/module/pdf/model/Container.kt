package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.listener.OnChildrenDrawListener
import cz.softcake.module.pdf.listener.OnContainerPreCalculateListener
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import kotlin.math.abs

interface ParentGetters : RectangularElementGetters {
    val document: PDDocument? get() = null
    val _parent: ParentGetters? get() = null
}

object SizeType {
    const val FILL_PARENT = -1f
    const val WRAP_CONTENT = -2f
}

abstract class Container(
        protected val _height: Float = 0f,
        protected val _width: Float = 0f,
        val children: MutableList<Element> = ArrayList(),
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        gravity: Int = 0,
        id: String? = null
) : RectangularElement(
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        gravity,
        id
), ParentGetters, OnChildrenDrawListener, OnContainerPreCalculateListener {

    override val _parent: ParentGetters?
        get() = this.parent

    override val document: PDDocument?
        get() = this.parent?.document

    override val startX: Float
        get() {
            val coefficient = horizontalGravityCoefficient
            return (this.parent?.let { it.startX + (it.width * coefficient) } ?: 0f) -
                    (this.width * coefficient) +
                    horizontalPaddingCoefficient +
                    shiftX
        }

    override val startY: Float
        get() {
            val coefficient = verticalGravityCoefficient
            return (this.parent?.let { it.startY + (it.height * coefficient) } ?: 0f) -
                    (this.height * coefficient) +
                    verticalPaddingCoefficient +
                    shiftY
        }

    override var height: Float = _height
        get() = if (field == SizeType.FILL_PARENT && this.parent != null) {
            this.parent!!.height - this.paddingTop - this.paddingBottom
        } else field

    override var width: Float = _width
        get() = if (field == SizeType.FILL_PARENT && this.parent != null) {
            this.parent!!.width - this.paddingLeft - this.paddingRight
        } else field

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

    override fun preCalculate() {
        this.onPreCalculateChildren()
        this.onPreCalculateWrapContent()
    }

    override fun onPreCalculateChildren() {
        children.filterIsInstance<RectangularElement>()
                .forEach { it.preCalculate() }
    }

    override fun onPreCalculateWrapContent() {
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
    override fun draw(contentStream: PDPageContentStream) {
        onChildrenDrawStarted(contentStream, children)
        onChildrenDraw(contentStream, children)
        onChildrenDrawFinished(contentStream, children)
        super.draw(contentStream)
    }

    @Throws(IOException::class)
    override fun onChildrenDraw(contentStream: PDPageContentStream, children: List<Element>) {
        for (element in children) {
            element.draw(contentStream)
        }
    }
}