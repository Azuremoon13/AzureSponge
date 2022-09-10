package xyz.azuremoon

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SpongeAbsorbEvent
import xyz.azuremoon.util.ConfigController
import xyz.azuremoon.util.LogTrans
import kotlin.math.sqrt

class ASEventListener : Listener {

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent) {
        LogTrans.info(e.blockReplacedState.type.toString())
        if (e.blockPlaced.type == Material.SPONGE && (e.blockReplacedState.type == Material.WATER || e.blockReplacedState.type == Material.LAVA)) {

            val drainArea = if (e.player.hasPermission("sponge.use")) {
                areaAround(e.block.location, ConfigController.spongeRadius)
            } else {
                areaAround(e.block.location, 5, "sphere")
            }

            drainArea.forEach { void ->
                when (void.type) {
                    Material.KELP_PLANT -> {
                        void.breakNaturally(); void.type = Material.AIR
                    }

                    Material.KELP -> {
                        void.breakNaturally(); void.type = Material.AIR
                    }

                    Material.SEAGRASS -> void.type = Material.AIR
                    Material.TALL_SEAGRASS -> void.type = Material.AIR
                    Material.WATER -> void.type = Material.AIR
                    Material.BUBBLE_COLUMN -> void.type = Material.AIR
                    Material.LAVA -> if (e.player.hasPermission("sponge.lava")) {
                        void.type = Material.AIR
                    }

                    else -> {}
                }
                if (void.blockData is Waterlogged && ConfigController.clearWaterlogged) {
                    val wl: Waterlogged = void.blockData as Waterlogged
                    if (wl.isWaterlogged) {
                        wl.isWaterlogged = false
                        void.blockData = wl
                        void.state.update()
                    }
                }
            }

            if (e.player.hasPermission("sponge.shield") && e.player.isSneaking) {
                val shieldArea = areaAround(e.blockPlaced.location, (ConfigController.shieldRadius), hollow = true)
                shieldArea.forEach {
                    when (it.type) {
                        Material.AIR -> it.type = Material.STRUCTURE_VOID
                        else -> {}
                    }
                }
            }
            if (e.player.hasPermission("sponge.dry")) {
                e.blockPlaced.type = Material.SPONGE
            } else {
                e.blockPlaced.type = Material.WET_SPONGE
            }
        }
    }

    @EventHandler
    fun onSpongeRemove(e: BlockBreakEvent) {
        if (e.block.type == Material.SPONGE || e.block.type == Material.WET_SPONGE) {
            areaAround(e.block.location, ConfigController.shieldRadius).forEach {
                when (it.type) {
                    Material.STRUCTURE_VOID -> it.type = Material.AIR
                    else -> {}
                }
            }
        }
    }

    @EventHandler
    fun spongeOverride(e: SpongeAbsorbEvent){
        e.isCancelled = true
    }

    private fun areaAround(
        location: Location,
        radius: Int,
        shape: String = ConfigController.clearShape,
        hollow: Boolean = false
        ): List<Block> {
        val area = mutableListOf<Block>()
        val range = -radius..radius
        range.forEach { x ->
            range.forEach { y ->
                range.forEach { z ->
                    if (shape == "cube") {
                        if (!hollow || ((x == -radius || x == radius) || (y == -radius || y == radius) || (z == -radius || z == radius))){
                            area.add(location.block.getRelative(x, y, z))
                        }
                    }

                    if (shape == "sphere") {
                        val distance = sqrt((x * x + y * y + z * z).toDouble())
                        if(distance <= radius && !(hollow && distance <= (radius - 1))) {
                            area.add(location.block.getRelative(x, y, z))
                            }
                        }
                    }
                }
            }
        return area
        }
}


