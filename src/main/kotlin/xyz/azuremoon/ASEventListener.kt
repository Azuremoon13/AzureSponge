package xyz.azuremoon

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import xyz.azuremoon.util.ConfigController

class ASEventListener : Listener {

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent){
        if (e.blockPlaced.type != Material.SPONGE || !e.player.hasPermission("sponge.use")) return
        sphereAround(e.blockPlaced.location, ConfigController.spongeRadius).forEach {
            when (it.type) {
                Material.WATER -> it.type = Material.AIR
                Material.LAVA -> if (e.player.hasPermission("sponge.lava")) {
                    it.type = Material.AIR
                }
                else -> {}
            }
        }
        if (e.player.hasPermission("sponge.dry")) {
            e.blockPlaced.type = Material.SPONGE
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
