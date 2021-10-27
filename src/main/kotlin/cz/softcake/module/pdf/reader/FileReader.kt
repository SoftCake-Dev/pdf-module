package cz.softcake.module.pdf.reader

import org.jetbrains.annotations.NotNull
import java.io.*
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets

object FileReader {
    @NotNull
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

    @NotNull
    internal fun readFontFromResource(path: String): InputStream {
        val classLoader: ClassLoader = this::class.java.classLoader
        val inputStream = classLoader.getResourceAsStream(path)

        if (inputStream != null) {
            return inputStream
        }

        throw RuntimeException("Missing font $path")
    }
}