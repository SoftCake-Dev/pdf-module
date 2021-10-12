package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.*
import cz.softcake.module.pdf.reader.FileReader
import org.apache.pdfbox.pdmodel.PDDocument
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URISyntaxException

fun JSONObject.toPdf(): Pdf {
    return Pdf(
            pages = this.getOrNull<JSONArray>("page")?.map { it.cast<JSONObject>().toPage() }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("page")?.toPage()?.let { mutableListOf(it) }
                    ?: mutableListOf()
    )
}

class Pdf(
        private val pages: MutableList<Page> = mutableListOf(),
) {

    val document: PDDocument = PDDocument()

    companion object {

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun readFromFile(name: String): Pdf {
            return this.readFromFile(name, true)
        }

        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun readFromFile(name: String, preCalculate: Boolean): Pdf {
            return FileReader.readJsonFromXmlFile(name)
                    .getOrThrow<JSONObject>("pdf")
                    .toPdf().also { if(preCalculate) it.preCalculate() }
        }
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

    fun findElementById(id: String): Element? {
        return pages.mapNotNull { it.findElementById(id) }
                .firstOrNull()
    }

    fun copy(): Pdf {
        return Pdf(
                pages = pages.map { it.copy() }.toMutableList()
        )
    }

    @Throws(IOException::class)
    private fun draw(): PDDocument {
        return PDDocument().also { document ->
            pages.forEach { it.draw(document) }
        }
    }

    @Throws(IOException::class)
    fun save(path: String) {
        this.draw().apply {
            save(path)
            close()
        }
    }

    @Throws(IOException::class)
    fun save(outputStream: OutputStream) {
        this.draw().apply {
            save(outputStream)
            close()
        }
    }

}