package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.adapter.ListContainerAdapter
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.net.URISyntaxException

fun JSONObject.toListContainer(): ListContainer {
    val padding = this.getOrNull<Float>("padding") ?: 0f

    return ListContainer(
            orientation = this.getOrNull<String>("orientation").toOrientation(),
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 0f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            paddingLeft = this.getOrNull<Float>("paddingLeft") ?: padding,
            paddingTop = this.getOrNull<Float>("paddingTop") ?: padding,
            paddingRight = this.getOrNull<Float>("paddingRight") ?: padding,
            paddingBottom = this.getOrNull<Float>("paddingBottom") ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

class ListContainer(
        orientation: Int = OrientationType.ORIENTATION_VERTICAL,
        strokeWidth: Float = 0f,
        strokeColor: Color = Color.BLACK,
        height: Float = 0f,
        width: Float = 0f,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        gravity: Int = 0,
        id: String? = null
) : LinearContainer(
        orientation,
        strokeWidth,
        strokeColor,
        height,
        width,
        mutableListOf(),
        paddingLeft,
        paddingTop,
        paddingRight,
        paddingBottom,
        gravity,
        id
) {

    public var adapter: ListContainerAdapter? = null

    @Throws(IOException::class)
    override fun onChildrenDrawStarted(contentStream: PDPageContentStream, children: List<Element>) {
        if (adapter != null) {
            try {
                val element = adapter!!.onCreateElement()
                for (i in 0 until adapter!!.itemCount) {
                    element?.copy()?.also {
                        it.parent = this
                        adapter!!.onBindElement(it, i)
                        if (it is Container) {
                            it.preCalculate()
                        }
                    }?.also(this::addChild)
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        super.preCalculate()
        super.onChildrenDrawStarted(contentStream, children)
    }

    override fun copy(): ListContainer {
        return ListContainer(
                orientation,
                strokeWidth,
                strokeColor,
                height,
                width,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                gravity,
                id
        ).also { it.adapter = this.adapter }
    }
}