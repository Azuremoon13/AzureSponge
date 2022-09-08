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
import kotlin.math.sqrt

class ASEventListener : Listener {

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent) {
        if (e.blockPlaced.type != Material.SPONGE) return

        val drainRadius = if (e.player.hasPermission("sponge.use")){
            ConfigController.spongeRadius
        } else { 4 }

        val drainArea = areaAround(e.block.location, drainRadius)

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
            val shieldArea = areaAround(e.blockPlaced.location, (ConfigController.shieldRadius))
            val voidArea = areaAround(e.blockPlaced.location, (ConfigController.shieldRadius - 1))
            shieldArea.forEach structure@{
                if (it in voidArea) return@structure
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
                        area.add(location.block.getRelative(x, y, z))
                    }
                    if (shape == "sphere") {
                        if (sqrt((x * x + y * y + z * z).toDouble()) <= radius) {
                            area.add(location.block.getRelative(x, y, z))
                            }
                        }
                    }
                }
            }
        return area
        }
}


