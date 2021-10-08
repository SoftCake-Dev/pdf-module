package cz.softcake.module.pdf.extensions

import org.json.JSONException
import org.json.JSONObject

inline fun <reified T> JSONObject.getOrThrow(key: String): T {
    return this.getOrNull(key) ?: throw JSONException("Key is missing")
}

inline fun <reified T> JSONObject.getOrNull(key: String): T? {
    return if(this.has(key)) {
        when (T::class) {
            Float::class -> this[key].let { it.castOrNull<Number>()?.toFloat() ?: it?.toString()?.toFloatOrNull() }
            Int::class -> this[key].let { it.castOrNull<Number>()?.toInt() ?: it?.toString()?.toIntOrNull() }
            String::class -> this[key].toString()
            else -> this[key]
        }?.castOrNull()
    } else null
}