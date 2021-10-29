package cz.softcake.module.pdf.element.text

import cz.softcake.module.pdf.element.RectangularElement
import cz.softcake.module.pdf.extensions.*
import cz.softcake.module.pdf.reader.FileReader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.io.InputStream

fun JSONObject.toText(): Text {
    val padding = this.getOrNull<String>("padding").toDimension()

    return Text(
            fontSize = this.getOrNull<Float>("fontSize") ?: 12f,
            fontPath = this.getOrNull<String>("font").toFontPath(this.getOrNull<String>("fontStyle")),
            textColor = this.getOrNull<String>("textColor").toColor(),
            text = this.getOrNull<String>("text"),
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

class Text(
        @NotNull private val fontSize: Float = 12f,
        @NotNull private val fontPath: String = "roboto".toFontPath(),
        @NotNull private val textColor: Color = Color.BLACK,
        var text: String? = null,
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
) {

    private var _font: PDFont? = null

    @get:NotNull
    private val fontStream: InputStream
        get() = FileReader.readFontFromResource(fontPath)

    @get:NotNull
    private val font: PDFont
        get() = _font ?: PDDocument().let { PDType0Font.load(it, fontStream) }

    @get:NotNull
    override val width: Float
        get() = try {
            font.getStringWidth(text) / 1000 * fontSize
        } catch (e: IOException) {
            0f
        }

    @get:NotNull
    override val height: Float
        get() = fontSize

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

    fun formatText(vararg objects: Any?) {
        text = text?.format(*objects)
    }

    override fun preCalculate() {
        if (_font == null && parent?.document != null) {
            _font = PDType0Font.load(parent?.document, fontStream)
        }
    }

    override fun onDrawStarted(contentStream: PDPageContentStream) {
        preCalculate()
    }

    @Throws(IOException::class)
    override fun onDraw(contentStream: PDPageContentStream) {
        contentStream.beginText()
        contentStream.setFont(font, fontSize)
        contentStream.newLineAtOffset(startX, startY)
        contentStream.setNonStrokingColor(textColor)
        contentStream.showText(text)
        contentStream.endText()
    }

    override fun onCopy(): Text {
        return Text(
                fontSize,
                fontPath,
                textColor,
                text,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                gravity,
                id
        )
    }
}