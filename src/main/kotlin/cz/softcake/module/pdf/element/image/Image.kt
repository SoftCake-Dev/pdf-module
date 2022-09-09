package cz.softcake.module.pdf.element.image

import cz.softcake.module.pdf.element.RectangularElement
import cz.softcake.module.pdf.element.VisibilityType
import cz.softcake.module.pdf.element.container.SizeType
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.jetbrains.annotations.NotNull
import java.awt.image.BufferedImage

open class Image(
        height: Float = SizeType.FILL_PARENT,
        width: Float = SizeType.FILL_PARENT,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        visibility: VisibilityType = VisibilityType.VISIBLE,
        gravity: Int = 0,
        id: String? = null
) : RectangularElement(
        if (height != SizeType.WRAP_CONTENT) height else 0f,
        if (width != SizeType.WRAP_CONTENT) width else 0f,
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        visibility,
        gravity,
        id
) {

    protected open val bufferedImage: BufferedImage? // TODO: Load from resource
        get() = null

    @get:NotNull
    protected val image: PDImageXObject?
        get() = if(bufferedImage != null) JPEGFactory.createFromImage(parent?.document ?: PDDocument(), bufferedImage) else null

    override fun onDraw(contentStream: PDPageContentStream) {
        if(image != null) {
            contentStream.drawImage(image, startX, startY, width, height)
        }
    }

    override fun onCopy(): Image {
        return Image(
                _height,
                _width,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                visibility,
                gravity,
                id
        )
    }
}