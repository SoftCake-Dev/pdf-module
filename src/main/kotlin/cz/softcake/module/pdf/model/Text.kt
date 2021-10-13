package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.getOrNull
import cz.softcake.module.pdf.extensions.toColor
import cz.softcake.module.pdf.extensions.toGravity
import cz.softcake.module.pdf.reader.FileReader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import java.awt.Color
import java.io.File
import java.io.IOException
import java.net.URISyntaxException

fun JSONObject.toText(): Text {
    val padding = this.getOrNull<Float>("padding") ?: 0f

    return Text(
            fontSize = this.getOrNull<Float>("fontSize") ?: 12f,
            fontFile = FileReader.getFontFile(
                    this.getOrNull<String>("font"),
                    this.getOrNull<String>("fontStyle")
            ),
            textColor = this.getOrNull<String>("textColor").toColor(),
            text = this.getOrNull<String>("text"),
            paddingLeft = this.getOrNull<Float>("paddingLeft") ?: padding,
            paddingTop = this.getOrNull<Float>("paddingTop") ?: padding,
            paddingRight = this.getOrNull<Float>("paddingRight") ?: padding,
            paddingBottom = this.getOrNull<Float>("paddingBottom") ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

class Text(
        @NotNull private val fontSize: Float = 12f,
        @NotNull private val fontFile: File = FileReader.getFontFile(),
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
            (font ?: PDDocument().let { PDType0Font.load(it, fontFile) }).getStringWidth(text) / 1000 * fontSize
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
            font = PDType0Font.load(parent?.document, fontFile)
        }
    }

    override fun onCopy(): Text {
        return Text(
                fontSize,
                fontFile,
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