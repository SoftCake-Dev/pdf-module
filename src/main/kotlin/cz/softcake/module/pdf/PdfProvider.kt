package cz.softcake.module.pdf

import java.io.IOException
import java.net.URISyntaxException

class PdfProvider private constructor(
        private val pdfTemplates: HashMap<String, Pdf>
) {

    companion object {
        @JvmStatic
        fun factory(): Factory {
            return Factory()
        }
    }

    // TODO: fix pdf copy (Error: copy of fill parent
    fun copyOfPdf(name: String): Pdf {
        return if (pdfTemplates.containsKey(name)) {
            pdfTemplates[name]!!.copy().apply {
                preCalculate()
            }
        } else throw RuntimeException("Template with $name not found")
    }

    class Factory internal constructor() {

        private val pdfTemplates: HashMap<String, Pdf> = hashMapOf()

        @Throws(IOException::class, URISyntaxException::class)
        fun putPdfTemplateFromFile(name: String, path: String): Factory {
            pdfTemplates[name] = Pdf.fromResource(path, false)
            return this
        }

        fun putPdfTemplate(name: String, pdf: Pdf): Factory {
            pdfTemplates[name] = pdf
            return this
        }

        fun create(): PdfProvider {
            return PdfProvider(pdfTemplates)
        }
    }
}