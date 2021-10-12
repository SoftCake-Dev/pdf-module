package cz.softcake.module.pdf.listener

import cz.softcake.module.pdf.model.RectangularElement

interface OnCopyListener {
    fun onCopy(): RectangularElement
}