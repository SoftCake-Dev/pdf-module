package cz.softcake.module.pdf.element.image

import cz.softcake.module.pdf.element.RectangularElement
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
        gravity: Int = 0,
        id: String? = null
) : RectangularElement(
        if(height != SizeType.WRAP_CONTENT) height else 0f,
        if(width != SizeType.WRAP_CONTENT) width else 0f,
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        gravity,
        id
) {

    protected open val bufferedImage: BufferedImage // TODO: Load from resource
        get() = BufferedImage(0, 0, 0)

    private var _image: PDImageXObject? = null

    @get:NotNull
    protected val image: PDImageXObject
        get() = _image ?: PDDocument().let { JPEGFactory.createFromImage(parent?.document, bufferedImage) }

    override fun preCalculate() {
        if (_image == null && parent?.document != null) {
            _image = JPEGFactory.createFromImage(parent?.document, bufferedImage)
        }

        if(_width == SizeType.WRAP_CONTENT) {
            _width = 0f
        }

        if(_height == SizeType.WRAP_CONTENT) {
            _height = 0f
        }
    }

    override fun onDrawStarted(contentStream: PDPageContentStream) {
        preCalculate()
    }

    override fun onDraw(contentStream: PDPageContentStream) {
        contentStream.drawImage(image, startX, startY, width, height)
    }

    override fun onCopy(): Image {
        return Image(
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
}