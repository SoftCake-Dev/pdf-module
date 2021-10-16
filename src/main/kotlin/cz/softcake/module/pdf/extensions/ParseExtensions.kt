package cz.softcake.module.pdf.extensions

import org.json.JSONObject
import org.json.XML

fun String.parseJsonFromXml(): JSONObject {
    return XML.toJSONObject(this)
}

fun String.replaceXmlTags(): String {
    return this
            .replace(Regex("<([a-zA-Z]+Page)\\s"), "<page type=\"\$1\" ")
            .replace(Regex("</([a-zA-Z]+Page)\\s*>"), "</page>")
            .replace(Regex("<(?!pdf|page|element)([a-zA-Z]+)\\s"), "<element type=\"\$1\" ")
            .replace(Regex("</(?!pdf|page|element)[a-zA-Z]+\\s*>"), "</element>")
}

fun String.parseSize(suffix: String): Float? {
    return this.substringBefore(suffix)
            .toFloatOrNull()
}