package xyz.azuremoon.util

import xyz.azuremoon.AzureSponge
import java.util.logging.Logger

object LogTrans {

    private val logger: Logger?
        get() = AzureSponge.instance
            ?.logger

    fun info(messge: String) = logger?.info(messge)

    fun warn(messge: String) = logger?.warning(messge)

    fun severe(messge: String) = logger?.severe(messge)

}