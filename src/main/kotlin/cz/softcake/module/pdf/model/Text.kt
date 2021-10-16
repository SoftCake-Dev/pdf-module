package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.getOrNull
import cz.softcake.module.pdf.extensions.toColor
import cz.softcake.module.pdf.extensions.toDimension
import cz.softcake.module.pdf.extensions.toGravity
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
            fontStream = FileReader.readFontFromResource(
                    this.getOrNull<String>("font"),
                    this.getOrNull<String>("fontStyle")
            ),
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
        @NotNull private val fontStream: InputStream = FileReader.readFontFromResource(),
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

    @NotNull
    private var font: PDFont? = null

    @get:NotNull
    override val width: Float
        get() = try {
            (font ?: PDDocument().let { PDType0Font.load(it, fontStream) }).getStringWidth(text) / 1000 * fontSize
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

    override fun preCalculate() {
        if (font == null && parent?.document != null) {
            font = PDType0Font.load(parent?.document, fontStream)
        }
    }

    override fun onCopy(): Text {
        return Text(
                fontSize,
                fontStream,
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