package cz.softcake.module.pdf.extensions

import cz.softcake.module.pdf.element.Barcode
import cz.softcake.module.pdf.element.container.Container
import cz.softcake.module.pdf.element.text.Text

fun Container.formatText(id: String, vararg objs: Any) {
    findById<Text>(id)?.formatText(*objs)
}

fun Container.setBarcode(id: String, obj: Any) {
    findById<Barcode>(id)?.setText(obj)
}

fun Container.setEmptyText(id: String) {
    findById<Text>(id)?.setText("")
}