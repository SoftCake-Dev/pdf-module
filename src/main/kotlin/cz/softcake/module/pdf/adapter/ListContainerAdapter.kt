package cz.softcake.module.pdf.adapter

import cz.softcake.module.pdf.model.Element
import org.jetbrains.annotations.Nullable
import java.io.IOException
import java.net.URISyntaxException

abstract class ListContainerAdapter {
    @Nullable
    @Throws(IOException::class, URISyntaxException::class)
    abstract fun onCreateElement(): Element?
    abstract fun onBindElement(element: Element, position: Int)
    abstract val itemCount: Int
}