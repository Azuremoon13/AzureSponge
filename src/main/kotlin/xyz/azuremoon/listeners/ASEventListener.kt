package xyz.azuremoon.listeners

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Waterlogged
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SpongeAbsorbEvent
import org.bukkit.plugin.Plugin
import xyz.azuremoon.AzureSponge
import xyz.azuremoon.util.ConfigController
import xyz.azuremoon.listeners.AdjListener.Companion.spongeRadiusAdj
import xyz.azuremoon.listeners.AdjListener.Companion.spongeShapeAdj
import xyz.azuremoon.listeners.AdjListener.Companion.fillRadiusAdj
import kotlin.math.sqrt

class ASEventListener : Listener {

    companion object {
        val grabBlocks = listOf(
            Material.GLOW_LICHEN,
            Material.WATER,
            Material.KELP_PLANT,
            Material.KELP,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.BUBBLE_COLUMN,
            Material.LAVA
        )

        val allowedReplaceBlocks = listOf(Material.LAVA, Material.WATER)

        val asPlugin = AzureSponge.instance as Plugin
    }

    @EventHandler
    fun onSpongePlace(e: BlockPlaceEvent) {


        when (e.blockPlaced.type) {
            Material.SPONGE -> {

                Bukkit.getScheduler().runTaskAsynchronously(asPlugin, Runnable {
                    val areas = drainCalculations(e)
                    Bukkit.getScheduler().runTask(asPlugin, Runnable {
                        areas.first.forEach { void ->
                            when (void.type) {
                                Material.KELP_PLANT -> {
                                    if (ConfigController.dropBlocks) {
                                        void.breakNaturally()
                                    }
                                    void.type = Material.AIR
                                }

                                Material.KELP -> {
                                    if (ConfigController.dropBlocks) {
                                        void.breakNaturally()
                                    }
                                    void.type = Material.AIR
                                }

                                Material.LAVA -> if (e.player.hasPermission("sponge.lava")) {
                                    void.type = Material.AIR
                                }

                                in grabBlocks -> void.type = Material.AIR

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
                        areas.second.forEach { shield ->
                            when (shield.type) {
                                Material.AIR -> shield.type = Material.STRUCTURE_VOID
                                else -> {}
                            }
                        }

                        if (!e.player.hasPermission("sponge.dry")) {
                            e.blockPlaced.type = Material.WET_SPONGE
                        }

                    })
                })

            }

            Material.WET_SPONGE -> {
                Bukkit.getScheduler().runTaskAsynchronously(asPlugin, Runnable {
                    val area = fillCalculations(e)
                    Bukkit.getScheduler().runTask(asPlugin, Runnable {
                        area.forEach { fill ->
                            when (fill.type) {
                                Material.AIR -> fill.type = Material.WATER
                                else -> {}
                            }
                        }
                        e.blockPlaced.type = Material.SPONGE
                    })
                })
            }

            else -> {
                return
            }
        }
    }

    @EventHandler
    fun onSpongeRemove(e: BlockBreakEvent) {
        if (e.block.type == Material.SPONGE || e.block.type == Material.WET_SPONGE) {
            areaAround(e.block.location, ConfigController.maxAdjRadius, "cube").forEach {
                when (it.type) {
                    Material.STRUCTURE_VOID -> it.type = Material.AIR
                    else -> {}
                }
            }
        }
    }

    @EventHandler
    fun spongeOverride(e: SpongeAbsorbEvent) {
        e.isCancelled = true
    }

    private fun drainCalculations(e: BlockPlaceEvent): Pair<List<Block>, List<Block>> {
        var drainArea = listOf<Block>()
        var shieldArea = listOf<Block>()

        if (e.blockReplacedState.type in allowedReplaceBlocks) {
            drainArea = when {
                e.player.hasPermission("sponge.adj") ->
                    areaAround(
                        e.block.location,
                        spongeRadiusAdj[e.player.uniqueId]?.first ?: ConfigController.spongeRadius,
                        spongeShapeAdj[e.player.uniqueId]?.second ?: ConfigController.clearShape
                    )

                e.player.hasPermission("sponge.use") ->
                    areaAround(e.block.location, ConfigController.spongeRadius)

                else ->
                    areaAround(e.block.location, 7, "default")
            }

            if (e.player.hasPermission("sponge.shield") && e.player.isSneaking) {
                shieldArea = when {
                    e.player.hasPermission("sponge.adj") ->
                        areaAround(
                            e.block.location,
                            spongeRadiusAdj[e.player.uniqueId]?.second ?: ConfigController.shieldRadius,
                            spongeShapeAdj[e.player.uniqueId]?.second ?: ConfigController.clearShape,
                            hollow = true,
                            blockOverride = listOf(Material.AIR)
                        )

                    else ->
                        areaAround(e.blockPlaced.location, (ConfigController.shieldRadius), hollow = true)
                }
            }
        }
        return Pair(drainArea, shieldArea)
    }

    private fun fillCalculations(e: BlockPlaceEvent): List<Block> {

        var fillArea = listOf<Block>()

        when (e.itemInHand.itemMeta?.lore.toString()) {
            "[An oddly saturated sponge]" -> {
                fillArea = when {
                    e.player.hasPermission("sponge.fillAdj") -> {
                        areaAround(
                            e.block.location,
                            fillRadiusAdj[e.player.uniqueId] ?: ConfigController.fillRadius,
                            spongeShapeAdj[e.player.uniqueId]?.second ?: ConfigController.clearShape,
                            blockOverride = listOf(Material.AIR)
                        )
                    }

                    e.player.hasPermission("sponge.fill") -> {
                        areaAround(
                            e.block.location,
                            ConfigController.fillRadius,
                            blockOverride = listOf(Material.AIR)
                        )
                    }

                    else -> {return  fillArea}
                }
            }
        }
        return  fillArea
    }


    // TODO blockOverride rework
    private fun areaAround(
        location: Location,
        radius: Int,
        shape: String = ConfigController.clearShape,
        hollow: Boolean = false,
        blockOverride: List<Material> = listOf()
    ): List<Block> {

        val area = mutableListOf<Block>()
        val range = -radius..radius
        val iterations = 1 until radius
        val nextArea = mutableListOf<Block>()
        val currentArea = mutableListOf<Block>()

        val blockFaces = listOf(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
        )

        when (shape) {
            "default" -> {
                currentArea.add(location.block)
                iterations.forEach { loop ->

                    if (loop != 1) {
                        currentArea.clear()
                        currentArea.addAll(nextArea)
                        nextArea.clear()
                    }

                    currentArea.forEach { block ->
                        blockFaces.forEach { face ->
                            val facedBlock = block.getRelative(face)
                            if ((facedBlock.type in grabBlocks || facedBlock.type in blockOverride) && facedBlock !in area) {
                                area.add(facedBlock); nextArea.add(facedBlock)
                            }
                        }
                    }
                }

                if (hollow) {
                    return nextArea
                }
            }

            else -> {
                range.forEach { x ->
                    range.forEach { y ->
                        range.forEach { z ->
                            when (shape) {
                                "cube" ->
                                    if (!hollow || ((x == -radius || x == radius) || (y == -radius || y == radius) || (z == -radius || z == radius))) {
                                        area.add(location.block.getRelative(x, y, z))
                                    }

                                "sphere" -> {
                                    val distance = sqrt((x * x + y * y + z * z).toDouble())
                                    if (distance <= radius && !(hollow && distance <= (radius - 1))) {
                                        area.add(location.block.getRelative(x, y, z))
                                    }
                                }

                                "cylinder" -> {
                                    val distance = sqrt((x * x + z * z).toDouble())
                                    if (distance <= radius && !(hollow && distance <= (radius - 1))) {
                                        area.add(location.block.getRelative(x, y, z))
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return area
    }

}


