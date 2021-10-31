package cz.softcake.module.pdf

import cz.softcake.module.pdf.element.Element
import cz.softcake.module.pdf.element.page.Page
import cz.softcake.module.pdf.element.page.toPage
import cz.softcake.module.pdf.extensions.*
import cz.softcake.module.pdf.reader.FileReader
import org.apache.pdfbox.pdmodel.PDDocument
import org.jetbrains.annotations.Nullable
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URISyntaxException
import java.util.*

fun JSONObject.toPdf(): Pdf {
    val pageType = this.getOrNull<String>("pageType")
    return Pdf(
            pages = this.getOrNull<JSONArray>("page")?.map { it.cast<JSONObject>().toPage(pageType) }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("page")?.toPage(pageType)?.let { mutableListOf(it) }
                    ?: mutableListOf()
    )
}

class Pdf(
        val pages: MutableList<Page> = mutableListOf(),
) {

    val document: PDDocument = PDDocument()

    companion object {

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromString(xml: String) = this.fromString(xml, true)

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromString(xml: String, preCalculate: Boolean): Pdf {
            return xml.replaceXmlTags()
                    .parseJsonFromXml()
                    .getOrThrow<JSONObject>("pdf")
                    .toPdf().also { if (preCalculate) it.preCalculate() }
        }

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromResource(path: String) = this.fromResource(path, true)

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun fromResource(path: String, preCalculate: Boolean) = this.fromString(FileReader.readJsonFromXmlResource(path), preCalculate)
    }

    init {
        pages.forEach {
            it.parent = this
        }
    }

    fun preCalculate() {
        pages.forEach { it.preCalculate() }
    }

    fun addPage(page: Page) {
        page.parent = this
        pages.add(page)
    }

    fun addAllPages(pages: List<Page>) {
        pages.forEach { addPage(it) }
    }

    @Nullable
    fun findElementById(id: String): Element? {
        return pages.mapNotNull { it.findElementById(id) }
                .firstOrNull()
    }

    @Nullable
    inline fun <reified T> findById(id: String): T? {
        return findElementById(id)?.castOrNull()
    }

    /**
     * TODO: for java usage
     */
    fun findOptionalById(id: String): Optional<Element> {
        return Optional.ofNullable(this.findElementById(id))
    }

    fun copy(): Pdf {
        return Pdf(
                pages = pages.map { it.copy() }.toMutableList()
        )
    }

    @Throws(IOException::class)
    private fun draw() {
        pages.forEach { it.draw() }
    }

    @Throws(IOException::class)
    fun save(path: String) {
        this.draw()
        document.apply {
            save(path)
            close()
        }
    }

    @Throws(IOException::class)
    fun save(outputStream: OutputStream) {
        this.draw()
        document.apply {
            save(outputStream)
            close()
        }
    }

}