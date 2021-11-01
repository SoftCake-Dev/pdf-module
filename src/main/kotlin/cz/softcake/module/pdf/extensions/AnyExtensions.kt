package cz.softcake.module.pdf.extensions

inline fun <reified T> Any.cast(): T {
    return this.castOrNull() ?: throw ClassCastException("Wrong cast exception")
}

inline fun <reified T> Any.castOrNull(): T? {
    return if (this is T) this else null
}
