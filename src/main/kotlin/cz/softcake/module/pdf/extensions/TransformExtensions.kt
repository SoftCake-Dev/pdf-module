package cz.softcake.module.pdf.extensions

import cz.softcake.module.pdf.model.GravityType
import cz.softcake.module.pdf.model.OrientationType
import cz.softcake.module.pdf.model.SizeType
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

fun String?.toSize(): Float {
    return when (this?.toLowerCase()) {
        null, "fill_parent" -> SizeType.FILL_PARENT
        "wrap_content" -> SizeType.WRAP_CONTENT
        else -> this.toFloatOrNull() ?: 0f
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
    // POINTS_PER_INCH = 72
    // POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH
    return when (this) {
        "A2" -> PDRectangle.A2
        "A3" -> PDRectangle.A3
        "A4" -> PDRectangle.A4
        "A5" -> PDRectangle.A5
        else -> PDRectangle.A4 // = [210 * POINTS_PER_MM, 297 * POINTS_PER_MM]
    }
}
