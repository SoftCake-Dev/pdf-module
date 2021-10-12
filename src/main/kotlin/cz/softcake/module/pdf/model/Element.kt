package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.getOrThrow
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.jetbrains.annotations.Nullable
import org.json.JSONObject
import java.io.IOException

fun JSONObject.toElement(): Element {
    return when(this.getOrThrow<String>("type")) {
        "absoluteContainer" -> this.toAbsoluteContainer()
        "linearContainer" -> this.toLinearContainer()
        "listContainer" -> this.toListContainer()
        "text" -> this.toText()
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