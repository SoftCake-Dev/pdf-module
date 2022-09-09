package cz.softcake.module.pdf.element.container

import cz.softcake.module.pdf.element.*
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.jetbrains.annotations.NotNull
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import kotlin.math.abs

fun JSONObject.toLinearContainer(): LinearContainer {
    val padding = this.getOrNull<String>("padding").toDimension()
    val orientation = this.getOrNull<String>("orientation").toOrientation()
    val height = this.getOrNull<String>("height").toSize()
    val width = this.getOrNull<String>("width").toSize()

    val jsonObjects = this.getOrNull<JSONArray>("element")?.map { it.cast() }
            ?: this.getOrNull<JSONObject>("element")?.let { listOf(it) }
            ?: mutableListOf()

    val children: MutableList<Element>

    if (jsonObjects.any { it.has("weigh") }) {
        val preprocessingElements = jsonObjects.map {
            var weigh = it.getOrNull<Int>("weigh")
            val element = it.toElement()

            if (element is RectangularElement) {
                if (weigh == null) {
                    if (element is Container) {
                        if (orientation and OrientationType.ORIENTATION_HORIZONTAL == OrientationType.ORIENTATION_HORIZONTAL && element.width == SizeType.FILL_PARENT) {
                            weigh = 1
                        } else if (element.height == SizeType.FILL_PARENT) {
                            weigh = 1
                        }
                    }
                } else if (weigh < 1) {
                    weigh = 1
                }
            }

            PreprocessingElement(weigh, element)
        }

        val weighSum = preprocessingElements.filter { it.weigh != null }.sumOf { it.weigh!! }

        children = preprocessingElements.map { preprocessingElement ->
            if (preprocessingElement.weigh == null) {
                preprocessingElement.element
            } else {
                preprocessingElement.element.let { element ->
                    AbsoluteContainer(
                            id = "$${element.id}_linearWeigh",
                            children = mutableListOf(element)
                    ).also {
                        if (orientation and OrientationType.ORIENTATION_HORIZONTAL == OrientationType.ORIENTATION_HORIZONTAL) {
                            it.height = if (element is Container) element.height else height
                        } else {
                            it.width = if (element is Container) element.width else width
                        }
                    }
                }.also { it.weighCoefficient = preprocessingElement.weigh.toFloat() / weighSum }
            }
        }.toMutableList()
    } else {
        children = jsonObjects.map { it.toElement() }.toMutableList()
    }

    return LinearContainer(
            orientation = orientation,
            strokeWidth = this.getOrNull<Float>("strokeWidth") ?: 0f,
            strokeColor = this.getOrNull<String>("strokeColor").toColor(),
            height = height,
            width = width,
            children = children,
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            visibility = this.getOrNull<String>("visibility").toVisibility(),
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

private data class PreprocessingElement(
        val weigh: Int? = null,
        val element: Element
)

object OrientationType {
    const val ORIENTATION_VERTICAL = 1
    const val ORIENTATION_HORIZONTAL = 2
}

open class LinearContainer(
        @NotNull val orientation: Int = OrientationType.ORIENTATION_VERTICAL,
        strokeWidth: Float = 0f,
        strokeColor: Color = Color.BLACK,
        children: MutableList<Element> = mutableListOf(),
        height: Float = SizeType.FILL_PARENT,
        width: Float = SizeType.FILL_PARENT,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        visibility: VisibilityType = VisibilityType.VISIBLE,
        gravity: Int = 0,
        id: String? = null
) : AbsoluteContainer(
        strokeWidth,
        strokeColor,
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

    override fun onPreCalculateWrapContent() {
        if (this.orientation and OrientationType.ORIENTATION_VERTICAL == OrientationType.ORIENTATION_VERTICAL && this.height == SizeType.WRAP_CONTENT) {
            this.height = this.children
                    .filterIsInstance<RectangularElement>()
                    .sumOf { (it.height + abs(it.verticalPaddingCoefficient)).toDouble() }
                    .toFloat()
        } else if (this.orientation and OrientationType.ORIENTATION_HORIZONTAL == OrientationType.ORIENTATION_HORIZONTAL && this.width == SizeType.WRAP_CONTENT) {
            this.width = this.children
                    .filterIsInstance<RectangularElement>()
                    .sumOf { (it.width + abs(it.horizontalPaddingCoefficient)).toDouble() }
                    .toFloat()
        }
        super.onPreCalculateWrapContent()
    }

    @Throws(IOException::class)
    override fun onChildrenDrawStarted(contentStream: PDPageContentStream, children: List<Element>) {
        val noWeighRectangularElements: List<RectangularElement> = children
                .filterIsInstance<RectangularElement>()
                .filter { it.weighCoefficient == null }

        val weighContainers: List<Container> = children
                .filterIsInstance<Container>()
                .filter { it.weighCoefficient != null }

        if (orientation and OrientationType.ORIENTATION_VERTICAL == OrientationType.ORIENTATION_VERTICAL) {
            val sumHeight = noWeighRectangularElements
                    .sumOf { (it.height + it.verticalPaddingCoefficient).toDouble() }
                    .toFloat()
            val realHeight: Float = this.height - sumHeight

            weighContainers.forEach { it.height = realHeight * it.weighCoefficient!! }
            noWeighRectangularElements.forEach { it.gravity = it.gravity and GravityType.GRAVITY_CENTER_HORIZONTAL or GravityType.GRAVITY_TOP }
        } else if (orientation and OrientationType.ORIENTATION_HORIZONTAL == OrientationType.ORIENTATION_HORIZONTAL) {
            val sumWidth = noWeighRectangularElements
                    .sumOf { (it.width + it.horizontalPaddingCoefficient).toDouble() }
                    .toFloat()
            val realWidth: Float = this.width - sumWidth

            weighContainers.forEach { it.width = realWidth * it.weighCoefficient!! }
            noWeighRectangularElements.forEach { it.gravity = it.gravity and GravityType.GRAVITY_CENTER_VERTICAL or GravityType.GRAVITY_LEFT }
        }
    }

    @Throws(IOException::class)
    override fun onChildrenDraw(contentStream: PDPageContentStream, children: List<Element>) {
        var shiftX = 0f
        var shiftY = 0f
        for (element in children) {
            element.shiftX = shiftX
            element.shiftY = shiftY
            element.draw(contentStream)
            if (element is RectangularElement) {
                if (orientation and OrientationType.ORIENTATION_VERTICAL == OrientationType.ORIENTATION_VERTICAL) {
                    shiftY -= element.height - element.verticalPaddingCoefficient
                } else if (orientation and OrientationType.ORIENTATION_HORIZONTAL == OrientationType.ORIENTATION_HORIZONTAL) {
                    shiftX += element.width + element.horizontalPaddingCoefficient
                }
            }
        }
    }

    override fun onCopy(): LinearContainer {
        return LinearContainer(
                orientation,
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