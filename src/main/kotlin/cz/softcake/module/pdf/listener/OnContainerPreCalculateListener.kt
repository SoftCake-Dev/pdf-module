package cz.softcake.module.pdf.listener

interface OnContainerPreCalculateListener {
    fun onPreCalculateChildren(): Unit? = null
    fun onPreCalculateWrapContent(): Unit? = null
}