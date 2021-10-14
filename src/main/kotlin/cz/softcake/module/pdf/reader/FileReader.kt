package cz.softcake.module.pdf.reader

import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

object FileReader {
    @Throws(IOException::class, URISyntaxException::class)
    internal fun readJsonFromXmlResource(path: String): String {
        val classLoader = this::class.java.classLoader
        val inputStream = classLoader.getResourceAsStream(path)
        if (inputStream != null) {

            val sb = StringBuilder()
            var line: String?
            val br = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            br.close()

            return sb.toString()
        }

        throw IOException("No xml definition of file with name $path was found!")
    }

    internal fun readFontFromResource(fontName: String? = null, fontStyle: String? = null): InputStream {
        val folderName = fontName?.toLowerCase().let {
            when (it) {
                "roboto" -> it
                else -> "roboto"
            }
        }

        var fileName = folderName
        val fontStyles = fontStyle?.split(" ")
        if (fontStyles?.isNotEmpty() == true) {
            fileName += fontStyles.sorted()
                    .distinct()
                    .joinToString(
                            prefix = "-",
                            separator = "-"
                    ) { it.toLowerCase() }
        }

        val classLoader: ClassLoader = FileReader.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream("$folderName/$fileName.ttf")

        if (inputStream != null) {
            return inputStream
        }

        throw RuntimeException("Missing font $folderName/$fileName.ttf")
    }
}