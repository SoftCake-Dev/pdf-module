package cz.softcake.module.pdf.model

import cz.softcake.module.pdf.extensions.*
import cz.softcake.module.pdf.reader.XmlReader
import org.apache.pdfbox.pdmodel.PDDocument
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URISyntaxException

fun JSONObject.toPdf(): Pdf {
    return Pdf(
            document = PDDocument(),
            pages = this.getOrNull<JSONArray>("page")?.map { it.cast<JSONObject>().toPage() }?.toMutableList()
                    ?: this.getOrNull<JSONObject>("page")?.toPage()?.let { mutableListOf(it) }
                    ?: mutableListOf()
    )
}

class Pdf(
        private val pages: MutableList<Page> = mutableListOf(),
        val document: PDDocument
) {

    companion object {
        @JvmStatic
        @Throws(IOException::class, URISyntaxException::class)
        fun readFromFile(name: String): Pdf {
            return XmlReader.readFromFile(name)
                    .getOrThrow<JSONObject>("pdf")
                    .toPdf()
        }
    }

    fun addPage(page: Page) {
        pages.add(page)
    }

    fun addAllPages(pages: List<Page>) {
        pages.forEach { addPage(it) }
    }

    fun findElementById(id: String): Element? {
        return pages.mapNotNull { it.findElementById(id) }
                .firstOrNull()
    }

    @Throws(IOException::class)
    fun save(path: String?) {
        document.also { document ->
            pages.forEach { it.draw(document) }
        }.apply {
            save(path)
            close()
        }
    }
}