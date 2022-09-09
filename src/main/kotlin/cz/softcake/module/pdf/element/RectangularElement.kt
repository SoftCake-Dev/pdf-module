package cz.softcake.module.pdf.element

import cz.softcake.module.pdf.element.container.SizeType
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.io.IOException

interface RectangularElementGetters {
    val startX: Float get() = 0f
    val startY: Float get() = 0f
    val height: Float get() = 0f
    val width: Float get() = 0f
}

enum class VisibilityType {
    VISIBLE,
    INVISIBLE,
    GONE
}

abstract class RectangularElement(
        protected var _height: Float = SizeType.FILL_PARENT,
        protected var _width: Float = SizeType.FILL_PARENT,
        val paddingLeft: Float = 0f,
        val paddingTop: Float = 0f,
        val paddingRight: Float = 0f,
        val paddingBottom: Float = 0f,
        val visibility: VisibilityType = VisibilityType.VISIBLE,
        gravity: Int = 0,
        id: String? = null
) : Element(
        gravity,
        id
), RectangularElementGetters {

    var weighCoefficient: Float? = null

    val verticalPaddingCoefficient
        get() = when {
            this.gravity and GravityType.GRAVITY_CENTER_VERTICAL == GravityType.GRAVITY_CENTER_VERTICAL -> paddingBottom - paddingTop
            this.gravity and GravityType.GRAVITY_BOTTOM == GravityType.GRAVITY_BOTTOM -> paddingBottom
            else -> -paddingTop
        }

    val horizontalPaddingCoefficient: Float
        get() = when {
            this.gravity and GravityType.GRAVITY_CENTER_HORIZONTAL == GravityType.GRAVITY_CENTER_HORIZONTAL -> paddingLeft - paddingRight
            this.gravity and GravityType.GRAVITY_RIGHT == GravityType.GRAVITY_RIGHT -> -paddingRight
            else -> paddingLeft
        }

    override var height: Float
        get() = if (_height == SizeType.FILL_PARENT && this.parent != null) {
            this.parent!!.height - this.paddingTop - this.paddingBottom
        } else _height
        internal set(value) {
            _height = value
        }

    override var width: Float
        get() = if (_width == SizeType.FILL_PARENT && this.parent != null) {
            this.parent!!.width - this.paddingLeft - this.paddingRight
        } else _width
        internal set(value) {
            _width = value
        }

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

    abstract fun onCopy(): RectangularElement

    override fun copy(): RectangularElement {
        return onCopy().also { it.weighCoefficient = this.weighCoefficient }
    }

    open fun preCalculate() = Unit

    @Throws(IOException::class)
    open fun onDrawStarted(contentStream: PDPageContentStream) = Unit

    @Throws(IOException::class)
    open fun onDraw(contentStream: PDPageContentStream) = Unit

    @Throws(IOException::class)
    open fun onDrawFinished(contentStream: PDPageContentStream) = Unit

    @Throws(IOException::class)
    override fun draw(contentStream: PDPageContentStream) {
        if(visibility != VisibilityType.GONE) {
            onDrawStarted(contentStream)
            if(visibility == VisibilityType.VISIBLE) {
                onDraw(contentStream)
            }
            onDrawFinished(contentStream)
        }
    }
}