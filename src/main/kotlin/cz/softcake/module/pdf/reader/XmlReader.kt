package cz.softcake.module.pdf.reader

import org.json.JSONObject
import org.json.XML
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

// TODO: Optimize xml reader and reading of elements

fun String.replaceXmlTags(): String {
    return this.replace("<absoluteContainer", "<element type=\"absoluteContainer\"")
            .replace("</absoluteContainer>", "</element>")
            .replace("<linearContainer", "<element type=\"linearContainer\"")
            .replace("</linearContainer>", "</element>")
            .replace("<listContainer", "<element type=\"listContainer\"")
            .replace("</listContainer>", "</listContainer>")
            .replace("<text", "<element type=\"text\"")
            .replace("</text>", "</element>")
}

object XmlReader {
    @Throws(IOException::class, URISyntaxException::class)
    fun readFromFile(name: String): JSONObject {
        val classLoader = this::class.java.classLoader
        val resource = classLoader.getResource(name)
        if (resource != null) {
            val file = File(resource.toURI())

            val sb = StringBuilder()
            var line: String?
            val br = BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            br.close()

            val xml = sb.toString().replaceXmlTags()

            return XML.toJSONObject(xml)
        }

        throw IOException("No xml definition of PDF file with name $name was found!")
    }
}