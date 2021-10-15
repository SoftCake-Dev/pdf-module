package cz.softcake.module.pdf.adapter

import cz.softcake.module.pdf.model.Element
import org.jetbrains.annotations.Nullable
import java.io.IOException
import java.net.URISyntaxException

interface ListContainerAdapter {
    @Nullable
    @Throws(IOException::class, URISyntaxException::class)
    fun onCreateElement(): Element?
    fun onBindElement(element: Element, position: Int)
    val itemCount: Int
}