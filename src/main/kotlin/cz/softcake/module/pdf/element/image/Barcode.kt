package cz.softcake.module.pdf.element

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.oned.Code128Writer
import com.google.zxing.oned.Code39Writer
import cz.softcake.module.pdf.element.container.toAbsoluteContainer
import cz.softcake.module.pdf.element.image.Image
import cz.softcake.module.pdf.extensions.*
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB

fun JSONObject.toBarcode(): Barcode {
    val padding = this.getOrNull<String>("padding").toDimension()

    return Barcode(
            barcodeEncoder = this.getOrNull<String>("codeType").toBarcodeEncoder(),
            text = this.getOrNull<String>("text"),
            height = this.getOrNull<String>("height").toSize(),
            width = this.getOrNull<String>("width").toSize(),
            paddingLeft = this.getOrNull<String>("paddingLeft")?.toDimension() ?: padding,
            paddingTop = this.getOrNull<String>("paddingTop")?.toDimension() ?: padding,
            paddingRight = this.getOrNull<String>("paddingRight")?.toDimension() ?: padding,
            paddingBottom = this.getOrNull<String>("paddingBottom")?.toDimension() ?: padding,
            visibility = this.getOrNull<String>("visibility").toVisibility(),
            gravity = this.getOrNull<String>("gravity").toGravity(),
            id = this.getOrNull<String>("id")
    )
}

interface BarcodeEncoder {
    fun encode(text: String, width: Float, height: Float): BufferedImage
}

class Code128Encoder : BarcodeEncoder {
    override fun encode(text: String, width: Float, height: Float) = Code128Writer()
            .encode(text, BarcodeFormat.CODE_128, width.toInt(), height.toInt())
            ?.let { MatrixToImageWriter.toBufferedImage(it) }
            ?: throw RuntimeException() // TODO: Throw custom exception
}

class Code39Encoder : BarcodeEncoder {
    override fun encode(text: String, width: Float, height: Float) = Code39Writer()
            .encode(text, BarcodeFormat.CODE_39, width.toInt(), height.toInt())
            ?.let { MatrixToImageWriter.toBufferedImage(it) }
            ?: throw RuntimeException() // TODO: Throw custom exception
}

class Barcode(
        @NotNull private val barcodeEncoder: BarcodeEncoder = Code128Encoder(),
        var text: String? = null,
        height: Float = 50f,
        width: Float = 100f,
        paddingLeft: Float = 0f,
        paddingTop: Float = 0f,
        paddingRight: Float = 0f,
        paddingBottom: Float = 0f,
        visibility: VisibilityType = VisibilityType.VISIBLE,
        gravity: Int = 0,
        id: String? = null
) : Image(
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

    override val bufferedImage: BufferedImage?
        get() = if (text?.isNotBlank() == true) barcodeEncoder.encode(text!!, width, height) else null

    fun setText(obj: Any?) {
        text = obj.toString()
    }

    override fun onCopy(): Image {
        return Barcode(
                barcodeEncoder,
                text,
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