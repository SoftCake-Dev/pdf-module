package cz.softcake.module.pdf.element

import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.io.IOException

interface RectangularElementGetters {
    val startX: Float get() = 0f
    val startY: Float get() = 0f
    val height: Float get() = 0f
    val width: Float get() = 0f
}

abstract class RectangularElement(
        val paddingLeft: Float = 0f,
        val paddingTop: Float = 0f,
        val paddingRight: Float = 0f,
        val paddingBottom: Float = 0f,
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

    abstract fun preCalculate()
    abstract fun onCopy(): RectangularElement

    override fun copy(): RectangularElement {
        return onCopy().also { it.weighCoefficient = this.weighCoefficient }
    }

    @Throws(IOException::class)
    open fun onDrawStarted(contentStream: PDPageContentStream): Unit? = null

    @Throws(IOException::class)
    open fun onDraw(contentStream: PDPageContentStream): Unit? = null

    @Throws(IOException::class)
    open fun onDrawFinished(contentStream: PDPageContentStream): Unit? = null

    @Throws(IOException::class)
    override fun draw(contentStream: PDPageContentStream) {
        onDrawStarted(contentStream)
        onDraw(contentStream)
        onDrawFinished(contentStream)
    }
}