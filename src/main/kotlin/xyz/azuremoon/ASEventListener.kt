package xyz.azuremoon

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import xyz.azuremoon.util.ConfigController

class ASEventListener : Listener {

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent){
        if (e.blockPlaced.type != Material.SPONGE || !e.player.hasPermission("sponge.use")) return
        sphereAround(e.blockPlaced.location, ConfigController.spongeRadius).forEach {void ->
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
        val  shieldArea = sphereAround(e.blockPlaced.location, (ConfigController.shieldRadius))
        val  voidArea = sphereAround(e.blockPlaced.location, (ConfigController.shieldRadius - 1))
        if (e.player.hasPermission("sponge.shield")){
            shieldArea.forEach structure@{
                if (it in voidArea) return@structure
                when (it.type){
                    Material.AIR -> it.type = Material.STRUCTURE_VOID
                    else -> {}
                }
            }
        }
        if (e.player.hasPermission("sponge.dry")) {
            e.blockPlaced.type = Material.SPONGE
        }
        else {e.blockPlaced.type = Material.WET_SPONGE}
    }

    @EventHandler
    fun onSpongeRemove(e: BlockBreakEvent){
        if ((e.block.type != Material.SPONGE || e.block.type != Material.WET_SPONGE) && !e.player.hasPermission("sponge.shield")) return
        sphereAround(e.block.location, ConfigController.shieldRadius).forEach{
            when (it.type){
                Material.STRUCTURE_VOID -> it.type = Material.AIR
                else -> {}
            }
        }
    }

    private fun sphereAround(location: Location, radius: Int): List<Block> {
        val sphere = mutableListOf<Block>()
        val range = -radius..radius
        range.forEach { x ->
            range.forEach { y ->
                range.forEach { z ->
                    sphere.add(location.block.getRelative(x, y, z))
                }
            }
        }
        return sphere
    }
}
