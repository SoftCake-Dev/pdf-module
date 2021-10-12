package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.getOrNull
import cz.softcake.module.pdf.extensions.toColor
import cz.softcake.module.pdf.extensions.toGravity
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

private fun getFontFromFile(fontName: String?, fontStyle: String?): PDFont {
    val folderName = fontName?.toLowerCase().let {
        when (it) {
            "roboto" -> it
            else -> "roboto"
        }
    }

    var fileName = folderName
    val fontStyles = fontStyle?.split(" ")
    if (fontStyles?.isNotEmpty() == true) {
        fileName += fontStyles.sorted()
                .distinct()
                .joinToString(
                        prefix = "-",
                        separator = "-"
                ) { it.toLowerCase() }
    }

    val document = PDDocument() // TODO: load font while drawing
    val classLoader: ClassLoader = document.javaClass.classLoader
    val resource = classLoader.getResource("$folderName/$fileName.ttf")

    if (resource != null) {
        try {
            return PDType0Font.load(document, File(resource.toURI()))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    throw RuntimeException("Missing font $folderName/$fileName.ttf")
}

fun JSONObject.toText(): Text {
    val padding = this.getOrNull<Float>("padding") ?: 0f

    return Text(
            fontSize = this.getOrNull<Float>("fontSize") ?: 12f,
            font = getFontFromFile(
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
        @NotNull private val font: PDFont = PDType1Font.HELVETICA,
        @NotNull private val textColor: Color = Color.BLACK,
        @NotNull private var text: String? = null,
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

    @get:NotNull
    override val width: Float
        get() = try {
            font.getStringWidth(text) / 1000 * fontSize
        } catch (e: IOException) {
            e.printStackTrace()
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

    @Throws(IOException::class)
    override fun onDraw(contentStream: PDPageContentStream) {
        contentStream.beginText()
        contentStream.setFont(font, fontSize)
        contentStream.newLineAtOffset(startX, startY)
        contentStream.setNonStrokingColor(textColor)
        contentStream.showText(text)
        contentStream.endText()
    }

    override fun copy(): Text {
        return Text(
                fontSize,
                font,
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