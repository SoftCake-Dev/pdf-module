package cz.softcake.module.pdf.extensions

import cz.softcake.module.pdf.element.*
import cz.softcake.module.pdf.element.container.OrientationType
import cz.softcake.module.pdf.element.container.SizeType
import org.apache.pdfbox.pdmodel.common.PDRectangle
import java.awt.Color

fun String?.toColor(): Color {
    return when (this) {
        "blue" -> Color.BLUE
        "black" -> Color.BLACK
        else -> Color.BLACK
    }
}

fun String?.toGravity(): Int {
    return this?.split(" ")
            ?.map {
                when (it.toLowerCase()) {
                    "left" -> GravityType.GRAVITY_LEFT
                    "top" -> GravityType.GRAVITY_TOP
                    "right" -> GravityType.GRAVITY_RIGHT
                    "bottom" -> GravityType.GRAVITY_BOTTOM
                    "center_horizontal" -> GravityType.GRAVITY_CENTER_HORIZONTAL
                    "center_vertical" -> GravityType.GRAVITY_CENTER_VERTICAL
                    "center" -> GravityType.GRAVITY_CENTER
                    else -> GravityType.GRAVITY_TOP or GravityType.GRAVITY_LEFT
                }
            }?.reduce { a: Int, b: Int -> a or b } ?: GravityType.GRAVITY_TOP or GravityType.GRAVITY_LEFT
}

fun Float.toPointsPerMillimeters(): Float {
    return this.times((1 / (10 * 2.54f) * 72))
}

fun Float.toMillimeters(): Float {
    return this.times(10)
}

fun String?.toDimension(): Float {
    return when {
        this == null -> 0f
        this.endsWith("cm") -> this.parseSize("cm")
                ?.toMillimeters()
                ?.toPointsPerMillimeters()
        this.endsWith("mm") -> this.parseSize("mm")
                ?.toPointsPerMillimeters()
        this.endsWith("pm") -> this.parseSize("pm")
        else -> 0f
    } ?: 0f
}

fun String?.toSize(): Float {
    return when (this?.toLowerCase()) {
        null, "fill_parent" -> SizeType.FILL_PARENT
        "wrap_content" -> SizeType.WRAP_CONTENT
        else -> this.toDimension()
    }
}

fun String?.toOrientation(): Int {
    return when (this?.toLowerCase()) {
        "horizontal" -> OrientationType.ORIENTATION_HORIZONTAL
        "vertical" -> OrientationType.ORIENTATION_VERTICAL
        else -> OrientationType.ORIENTATION_VERTICAL
    }
}

fun String?.toPageSize(): PDRectangle {
    return when (this) {
        "A2" -> PDRectangle.A2
        "A3" -> PDRectangle.A3
        "A4" -> PDRectangle.A4
        "A5" -> PDRectangle.A5
        else -> PDRectangle.A4
    }
}

fun String?.toFontPath(fontStyle: String? = null): String {
    val folderName = this?.toLowerCase().let {
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
    return "$folderName/$fileName.ttf"
}

fun String?.toBarcodeEncoder(): BarcodeEncoder {
    return when (this) {
        "code_128" -> Code128Encoder()
        "code_39" -> Code39Encoder()
        else -> Code128Encoder()
    }
}
