package xyz.azuremoon.util

import xyz.azuremoon.AzureSponge

object ConfigController {

    private const val SPONGERADIUS = "spongeRadius"
    private const val SHIELDRADIUS = "shieldRadius"
    private const val CLEARWATERLOG = "clearWaterLog"

    private const val DEFAULT_SPONGERADIUS = 20
    private const val DEFAULT_SHIELDRADIUS = 10
    private const val DEFAULT_CLEARWATERLOG = true

    val spongeRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(SPONGERADIUS, DEFAULT_SPONGERADIUS)
                ?.takeIf { it >= 0 }
                ?: DEFAULT_SPONGERADIUS
        }

    val shieldRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(SHIELDRADIUS, DEFAULT_SHIELDRADIUS)
                ?.takeIf { it >= 0 }
                ?: DEFAULT_SHIELDRADIUS
        }

    val clearWaterlogged: Boolean
        get() {
            return AzureSponge.instance
                ?.config
                ?.getBoolean(CLEARWATERLOG, DEFAULT_CLEARWATERLOG)
                ?: DEFAULT_CLEARWATERLOG
        }
}