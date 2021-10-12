package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.util.ArrayList

fun JSONObject.toAbsoluteContainer(): AbsoluteContainer {
    val padding = this.getOrNull<Float>("padding") ?: 0f

    return AbsoluteContainer(
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 0f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            children = this.getOrNull<JSONArray>("element")?.map { it.cast<JSONObject>().toElement() }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("element")?.toElement()?.let { mutableListOf(it) }
                    ?: mutableListOf(),
            paddingLeft = this.getOrNull<Float>("paddingLeft") ?: padding,
            paddingTop = this.getOrNull<Float>("paddingTop") ?: padding,
            paddingRight = this.getOrNull<Float>("paddingRight") ?: padding,
            paddingBottom = this.getOrNull<Float>("paddingBottom") ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

open class AbsoluteContainer(
        val strokeWidth: Float = 0f,
        val strokeColor: Color = Color.BLACK,
        height: Float = 0f,
        width: Float = 0f,
        children: MutableList<Element> = ArrayList(),
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        gravity: Int = 0,
        id: String? = null
) : Container(
        height,
        width,
        children,
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
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
                _height,
                _width,
                children.map { it.copy() }.toMutableList(),
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                gravity,
                id
        )
    }
}