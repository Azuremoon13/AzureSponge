package xyz.azuremoon

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

import xyz.azuremoon.util.LogTrans
import xyz.azuremoon.util.ConfigController

class ASEventListener : Listener {

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent){
        if (e.blockPlaced.type != Material.SPONGE) return
        for(block : Block in sphereAround(e.blockPlaced.location, ConfigController.spongeRadius))
            if (block.type == Material.WATER) {block.setType(Material.AIR)}
            else{if (block.type == Material.LAVA && e.player.hasPermission("Sponge.lava") ) block.setType(Material.AIR)}
    }

    private fun sphereAround(location: Location, radius: Int): Set<Block> {
        val sphere: MutableSet<Block> = HashSet()
        val center = location.block
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val b = center.getRelative(x, y, z)
                    if (center.location.distance(b.location) <= radius) {
                        sphere.add(b)
                    }
                }
            }
        }
        return sphere
    }
}
