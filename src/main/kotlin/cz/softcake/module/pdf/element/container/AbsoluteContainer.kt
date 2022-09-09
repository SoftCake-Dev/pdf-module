package cz.softcake.module.pdf.element.container

import cz.softcake.module.pdf.element.Element
import cz.softcake.module.pdf.element.VisibilityType
import cz.softcake.module.pdf.element.toElement
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.util.ArrayList

fun JSONObject.toAbsoluteContainer(): AbsoluteContainer {
    val padding = this.getOrNull<String>("padding").toDimension()

    return AbsoluteContainer(
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 0f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            children = this.getOrNull<JSONArray>("element")?.map { it.cast<JSONObject>().toElement() }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("element")?.toElement()?.let { mutableListOf(it) }
                    ?: mutableListOf(),
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            visibility = this.getOrNull<String>("visibility").toVisibility(),
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

open class AbsoluteContainer(
        val strokeWidth: Float = 0f,
        val strokeColor: Color = Color.BLACK,
        children: MutableList<Element> = ArrayList(),
        height: Float = SizeType.FILL_PARENT,
        width: Float = SizeType.FILL_PARENT,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        visibility: VisibilityType = VisibilityType.VISIBLE,
        gravity: Int = 0,
        id: String? = null
) : Container(
        children,
        height,
        width,
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        visibility,
        gravity,
        id
) {

    @Throws(IOException::class)
    override fun onDraw(contentStream: PDPageContentStream) {
        if (strokeWidth > 0) {
            contentStream.setLineWidth(strokeWidth)
            contentStream.setStrokingColor(strokeColor)
            contentStream.addRect(this.startX, this.startY, this.width, this.height)
            contentStream.stroke()
        }
    }

    override fun onCopy(): AbsoluteContainer {
        return AbsoluteContainer(
                strokeWidth,
                strokeColor,
                children.map { it.copy() }.toMutableList(),
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