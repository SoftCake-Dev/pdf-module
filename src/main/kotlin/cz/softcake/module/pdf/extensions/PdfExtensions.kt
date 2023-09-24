package cz.softcake.module.pdf.extensions

import cz.softcake.module.pdf.Pdf
import cz.softcake.module.pdf.element.Barcode
import cz.softcake.module.pdf.element.container.ListContainer
import cz.softcake.module.pdf.element.container.ListContainerAdapter
import cz.softcake.module.pdf.element.text.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Pdf.formatText(id: String, vararg objs: Any) {
    findById<Text>(id)?.formatText(*objs)
}

fun Pdf.setBarcode(id: String, obj: Any) {
    findById<Barcode>(id)?.setText(obj)
}

fun Pdf.setEmptyText(id: String) {
    findById<Text>(id)?.setText("")
}

fun Pdf.formatDateTime(id: String, dateTime: LocalDateTime, format: String) {
    findById<Text>(id)?.also {
        val formatter = DateTimeFormatter.ofPattern(format)
        it.setText(dateTime.format(formatter))
    }
}

fun Pdf.setListContainerAdapter(id: String, adapter: ListContainerAdapter) {
    findById<ListContainer>(id)?.adapter = adapter
}