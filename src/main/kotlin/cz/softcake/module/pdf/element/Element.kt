package cz.softcake.module.pdf.element

import cz.softcake.module.pdf.element.container.ParentGetters
import cz.softcake.module.pdf.element.container.toAbsoluteContainer
import cz.softcake.module.pdf.element.container.toLinearContainer
import cz.softcake.module.pdf.element.container.toListContainer
import cz.softcake.module.pdf.element.shape.toSeparator
import cz.softcake.module.pdf.element.text.toText
import cz.softcake.module.pdf.extensions.getOrThrow
import cz.softcake.module.pdf.extensions.parseJsonFromXml
import cz.softcake.module.pdf.extensions.replaceXmlTags
import cz.softcake.module.pdf.reader.FileReader
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.jetbrains.annotations.Nullable
import org.json.JSONObject
import java.io.IOException
import java.net.URISyntaxException

fun JSONObject.toElement(): Element {
    return when (this.getOrThrow<String>("type")) {
        "absoluteContainer" -> this.toAbsoluteContainer()
        "linearContainer" -> this.toLinearContainer()
        "listContainer" -> this.toListContainer()
        "text" -> this.toText()
        "barcode" -> this.toBarcode()
        "separator" -> this.toSeparator()
        else -> this.toAbsoluteContainer()
    }
}

object GravityType {
    const val GRAVITY_LEFT = 0b0001
    const val GRAVITY_RIGHT = 0b0010
    const val GRAVITY_CENTER_HORIZONTAL = GRAVITY_LEFT or GRAVITY_RIGHT
    const val GRAVITY_TOP = 0b0100
    const val GRAVITY_BOTTOM = 0b1000
    const val GRAVITY_CENTER_VERTICAL = GRAVITY_TOP or GRAVITY_BOTTOM
    const val GRAVITY_CENTER = GRAVITY_CENTER_VERTICAL or GRAVITY_CENTER_HORIZONTAL
}

abstract class Element(
        var gravity: Int = 0,
        @Nullable val id: String? = null
) {

    companion object {

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromString(xml: String) = this.fromString(xml, true)

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromString(xml: String, preCalculate: Boolean): Element {
            return xml.replaceXmlTags()
                    .parseJsonFromXml()
                    .getOrThrow<JSONObject>("element")
                    .toElement().also { if(preCalculate && it is RectangularElement) it.preCalculate() }
        }

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromResource(path: String) = this.fromResource(path, true)

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromResource(path: String, preCalculate: Boolean) = this.fromString(FileReader.readJsonFromXmlResource(path), preCalculate)
    }

    @Nullable
    var parent: ParentGetters? = null
    var shiftX: Float = 0f
    var shiftY: Float = 0f

    val verticalGravityCoefficient: Float
        get() = when {
            gravity and GravityType.GRAVITY_CENTER_VERTICAL == GravityType.GRAVITY_CENTER_VERTICAL -> 0.5f
            gravity and GravityType.GRAVITY_BOTTOM == GravityType.GRAVITY_BOTTOM -> 0f
            else -> 1f
        }

    val horizontalGravityCoefficient: Float
        get() = when {
            gravity and GravityType.GRAVITY_CENTER_HORIZONTAL == GravityType.GRAVITY_CENTER_HORIZONTAL -> 0.5f
            gravity and GravityType.GRAVITY_RIGHT == GravityType.GRAVITY_RIGHT -> 1f
            else -> 0f
        }

    @Throws(IOException::class)
    abstract fun draw(contentStream: PDPageContentStream)
    abstract fun copy(): Element
}