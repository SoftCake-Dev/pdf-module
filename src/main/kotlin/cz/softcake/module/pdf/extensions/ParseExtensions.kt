package cz.softcake.module.pdf.extensions

import org.json.JSONObject
import org.json.XML

fun String.parseJsonFromXml(): JSONObject {
    return XML.toJSONObject(this)
}

fun String.replaceXmlTags(): String {
    return this
            .replace("<absolutePage", "<page type=\"absolutePage\"")
            .replace("</absolutePage>", "</page>")
            .replace("<linearPage", "<page type=\"linearPage\"")
            .replace("</linearPage>", "</page>")
            .replace("<absoluteContainer", "<element type=\"absoluteContainer\"")
            .replace("</absoluteContainer>", "</element>")
            .replace("<linearContainer", "<element type=\"linearContainer\"")
            .replace("</linearContainer>", "</element>")
            .replace("<listContainer", "<element type=\"listContainer\"")
            .replace("</listContainer>", "</listContainer>")
            .replace("<text", "<element type=\"text\"")
            .replace("</text>", "</element>")
}