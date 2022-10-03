package xyz.azuremoon.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import xyz.azuremoon.commands.CommandController.Commands.Companion.shapeList
import xyz.azuremoon.listeners.AdjListener.Companion.fillRadiusAdj
import xyz.azuremoon.listeners.AdjListener.Companion.spongeRadiusAdj
import xyz.azuremoon.listeners.AdjListener.Companion.spongeShapeAdj
import xyz.azuremoon.util.ConfigController
import xyz.azuremoon.util.LogTrans



object CommandController {

    enum class Commands {
        SPONGEADJ;

        companion object {

            val shapeList = listOf(
                Triple(Material.HEART_OF_THE_SEA, "default", "Blocked by walls"),
                Triple(Material.SLIME_BLOCK, "cube", "NOT Blocked by walls"),
                Triple(Material.SLIME_BALL, "sphere", "NOT Blocked by walls"),
                Triple(Material.SUNFLOWER, "cylinder", "NOT Blocked by walls")
            )

            fun commandKey(key: String): Commands? = try {
                valueOf(key.uppercase().replace("-", "_"))
            } catch (e: Exception) {
                null
            }
        }
    }

    fun commandRoute(sender: CommandSender, command: Command): Boolean {
        return when (Commands.commandKey(command.name)) {
            Commands.SPONGEADJ -> configGui(sender)
            else -> false
        }
    }

    private fun configGui(sender: CommandSender): Boolean {
        if (sender !is Player) {
            LogTrans.warn("$sender is not a player")
            return false
        }
        val configGui: Inventory = Bukkit.createInventory(sender, 27, "Sponge ConfigGui")

        val range = 0..26

        val shield = ItemStack(Material.SPONGE, spongeRadiusAdj[sender.uniqueId]?.second ?: ConfigController.shieldRadius)
        val ssMeta = shield.itemMeta
        ssMeta!!.setDisplayName("Shield Radius")
        shield.itemMeta = ssMeta

        val sponge = ItemStack(Material.WET_SPONGE, spongeRadiusAdj[sender.uniqueId]?.first ?: ConfigController.spongeRadius)
        val srMeta = sponge.itemMeta
        srMeta!!.setDisplayName("Absorption Radius")
        sponge.itemMeta = srMeta

        val fill = ItemStack(Material.WATER_BUCKET, fillRadiusAdj[sender.uniqueId] ?: ConfigController.fillRadius)
        val fMeta = fill.itemMeta
        fMeta!!.setDisplayName("Fill Radius")
        fill.itemMeta = fMeta

        val green = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
        val gMeta = green.itemMeta
        gMeta!!.setDisplayName("+1")
        green.itemMeta = gMeta

        val red = ItemStack(Material.RED_STAINED_GLASS_PANE)
        val rMeta = red.itemMeta
        rMeta!!.setDisplayName("-1")
        red.itemMeta = rMeta

        val left = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
        val lMeta = left.itemMeta
        lMeta!!.setDisplayName("<-")
        left.itemMeta = lMeta

        val name = ConfigController.clearShape
        var nameMaterial = Material.AIR
        var nameLore = ""
        when (name){
            "default" -> {nameMaterial = shapeList[0].first; nameLore = shapeList[0].third}
            "cube" -> {nameMaterial = shapeList[1].first; nameLore = shapeList[1].third}
            "sphere" -> {nameMaterial = shapeList[2].first; nameLore = shapeList[2].third}
            "cylinder" -> {nameMaterial = shapeList[3].first; nameLore = shapeList[3].third}
        }

        val shape = ItemStack(spongeShapeAdj[sender.uniqueId]?.first ?: nameMaterial )
        val sMeta = shape.itemMeta
        sMeta!!.setDisplayName(spongeShapeAdj[sender.uniqueId]?.second ?: name)
        sMeta.lore = mutableListOf(spongeShapeAdj[sender.uniqueId]?.third ?: nameLore)
        shape.itemMeta = sMeta

        val right = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
        val riMeta = right.itemMeta
        riMeta!!.setDisplayName("->")
        right.itemMeta = riMeta

        when {
            sender.hasPermission("sponge.fillAdj") -> {
                val greenSlots = listOf(1, 5, 7)
                val redSlots = listOf(19, 23, 25)

                range.forEach { slot ->
                    when (slot) {
                        in redSlots -> configGui.setItem(slot, red)
                        in greenSlots -> configGui.setItem(slot, green)
                        10 -> configGui.setItem(slot, shield)
                        14 -> configGui.setItem(slot, sponge)
                        16 -> configGui.setItem(slot, fill)
                        3 -> configGui.setItem(slot, right)
                        12 -> configGui.setItem(slot, shape)
                        21 -> configGui.setItem(slot, left)
                    }
                }
            }

            sender.hasPermission("sponge.shield") -> {
                val greenSlots = listOf(2, 6)
                val redSlots = listOf(20, 24)

                range.forEach { slot ->
                    when (slot) {
                        in redSlots -> configGui.setItem(slot, red)
                        in greenSlots -> configGui.setItem(slot, green)
                        11 -> configGui.setItem(slot, shield)
                        15 -> configGui.setItem(slot, sponge)
                        4 -> configGui.setItem(slot, right)
                        13 -> configGui.setItem(slot, shape)
                        22 -> configGui.setItem(slot, left)
                    }
                }
            }

            else -> {
                range.forEach { slot ->
                    when (slot) {
                        24 -> configGui.setItem(slot, red)
                        15 -> configGui.setItem(slot, sponge)
                        6 -> configGui.setItem(slot, green)
                        20 -> configGui.setItem(slot, left)
                        11 -> configGui.setItem(slot, shape)
                        2 -> configGui.setItem(slot, right)
                    }
                }
            }
        }

        sender.openInventory(configGui)

        return true
    }

}


