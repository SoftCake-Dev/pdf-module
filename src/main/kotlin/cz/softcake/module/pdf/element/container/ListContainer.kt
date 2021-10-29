package cz.softcake.module.pdf.element.container

import cz.softcake.module.pdf.element.*
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.jetbrains.annotations.Nullable
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.net.URISyntaxException

fun JSONObject.toListContainer(): ListContainer {
    val padding = this.getOrNull<String>("padding").toDimension()

    return ListContainer(
            orientation = this.getOrNull<String>("orientation").toOrientation(),
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 0f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

interface ListContainerAdapter {
    @Nullable
    @Throws(IOException::class, URISyntaxException::class)
    fun onCreateElement(): Element?
    fun onBindElement(element: Element, position: Int)
    val itemCount: Int
}

class ListContainer(
        orientation: Int = OrientationType.ORIENTATION_VERTICAL,
        strokeWidth: Float = 0f,
        strokeColor: Color = Color.BLACK,
        height: Float = SizeType.FILL_PARENT,
        width: Float = SizeType.FILL_PARENT,
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

    override fun preCalculate() = Unit

    @Throws(IOException::class)
    override fun onChildrenDrawStarted(contentStream: PDPageContentStream, children: List<Element>) {
        if (adapter != null) {
            try {
                val element = adapter!!.onCreateElement()
                for (i in 0 until adapter!!.itemCount) {
                    element?.copy()?.also {
                        it.parent = this
                        adapter!!.onBindElement(it, i)
                        if (it is RectangularElement) {
                            it.preCalculate()
                        }
                    }?.also(this::addChild)
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        super.onPreCalculateWrapContent()
        super.onChildrenDrawStarted(contentStream, children)
    }

    override fun onCopy(): ListContainer {
        return ListContainer(
                orientation,
                strokeWidth,
                strokeColor,
                _height,
                _width,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
                gravity,
                id
        ).also { it.adapter = this.adapter }
    }
}