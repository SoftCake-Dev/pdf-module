package cz.softcake.module.pdf.element.shape

import cz.softcake.module.pdf.element.RectangularElement
import cz.softcake.module.pdf.element.container.OrientationType
import cz.softcake.module.pdf.element.container.SizeType
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import java.awt.Color

fun JSONObject.toSeparator(): Separator {
    val padding = this.getOrNull<String>("padding").toDimension()

    return Separator(
            orientation = this.getOrNull<String>("orientation").toOrientation(OrientationType.ORIENTATION_HORIZONTAL),
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 1f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

class Separator(
        @NotNull val orientation: Int = OrientationType.ORIENTATION_HORIZONTAL,
        @NotNull val strokeWidth: Float = 1f,
        @NotNull val strokeColor: Color = Color.BLACK,
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
) {

    override var height: Float
        get() = if(orientation == OrientationType.ORIENTATION_HORIZONTAL) strokeWidth else super.height
        set(value) { super.height = value }

    override var width: Float
        get() = if(orientation == OrientationType.ORIENTATION_VERTICAL) strokeWidth else super.width
        set(value) { super.width = value }

    override fun preCalculate() {

    }

    override fun onCopy(): RectangularElement {
        return Separator(
                orientation,
                strokeWidth,
                strokeColor,
                _height,
                _width,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                gravity,
                id
        )
    }

    override fun onDraw(contentStream: PDPageContentStream) {
        contentStream.setLineWidth(strokeWidth)
        contentStream.setStrokingColor(strokeColor)

        if (orientation == OrientationType.ORIENTATION_VERTICAL) {
            contentStream.moveTo(startX, startY)
            contentStream.lineTo(startX, startY + height)
        } else {
            contentStream.moveTo(startX, startY)
            contentStream.lineTo(startX + width, startY)
        }

        contentStream.stroke()
    }
}