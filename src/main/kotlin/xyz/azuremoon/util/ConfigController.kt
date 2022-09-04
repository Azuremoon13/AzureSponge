package xyz.azuremoon.util

import xyz.azuremoon.AzureSponge

object ConfigController {

    private const val SPONGERADIUS = "spongeRadius"

    private const val DEFAULT_SPONGERADIUS = 25

    val spongeRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(SPONGERADIUS, DEFAULT_SPONGERADIUS)
                ?.takeIf { it >= 0 }
                ?: DEFAULT_SPONGERADIUS
        }
}