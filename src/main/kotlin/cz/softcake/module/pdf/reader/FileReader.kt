package cz.softcake.module.pdf.reader

import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

object FileReader {
    @Throws(IOException::class, URISyntaxException::class)
    internal fun readJsonFromXmlFile(name: String): String {
        val classLoader = this::class.java.classLoader
        val inputStream = classLoader.getResourceAsStream(name)
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

        throw IOException("No xml definition of file with name $name was found!")
    }

    internal fun getFontFile(fontName: String? = null, fontStyle: String? = null): File {
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
        val resource = classLoader.getResource("$folderName/$fileName.ttf")

        if (resource != null) {
            try {
                return File(resource.toURI())
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }

        throw RuntimeException("Missing font $folderName/$fileName.ttf")
    }
}