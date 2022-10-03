package xyz.azuremoon.util

import xyz.azuremoon.AzureSponge

object ConfigController {

    private const val SPONGERADIUS = "spongeRadius"
    private const val FILLRADIUS = "fillRadius"
    private const val SHIELDRADIUS = "shieldRadius"
    private const val CLEARWATERLOG = "clearWaterLog"
    private const val DROPBLOCKS = "dropBlocks"
    private const val CLEARSHAPE = "clearShape"
    private const val MAXADJRADIUS = "maxAdjRadius"

    private const val DEFAULT_SPONGERADIUS = 20
    private const val DEFAULT_FILLRADIUS = 10
    private const val DEFAULT_SHIELDRADIUS = 10
    private const val DEFAULT_CLEARWATERLOG = true
    private const val DEFAULT_DROPBLOCKS = true
    private const val DEFAULT_CLEARSHAPE = "default"
    private const val DEFAULT_MAXADJRADIUS = 30

    val spongeRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(SPONGERADIUS, DEFAULT_SPONGERADIUS)
                ?.takeIf { it >= 0 }
                ?: DEFAULT_SPONGERADIUS
        }

    val fillRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(FILLRADIUS, DEFAULT_FILLRADIUS)
                ?.takeIf { it >= 0 }
                ?: DEFAULT_FILLRADIUS
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

    val dropBlocks: Boolean
        get() {
            return AzureSponge.instance
                ?.config
                ?.getBoolean(DROPBLOCKS, DEFAULT_DROPBLOCKS)
                ?: DEFAULT_DROPBLOCKS
        }

    val clearShape: String
        get() {
            return AzureSponge.instance
                ?.config
                ?.getString(CLEARSHAPE, DEFAULT_CLEARSHAPE)
                ?: DEFAULT_CLEARSHAPE
        }

    val maxAdjRadius: Int
        get() {
            return AzureSponge.instance
                ?.config
                ?.getInt(MAXADJRADIUS, DEFAULT_MAXADJRADIUS)
                ?.takeIf { it >= 0 }
                ?.takeIf { it >= shieldRadius }
                ?: DEFAULT_MAXADJRADIUS
        }
}