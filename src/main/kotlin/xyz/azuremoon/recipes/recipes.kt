package xyz.azuremoon.recipes

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.Plugin
import xyz.azuremoon.AzureSponge

fun sSponge() {
    val sponge = ItemStack(Material.WET_SPONGE)
    val sMeta = sponge.itemMeta
    sMeta?.setDisplayName("Saturated Sponge")
    sMeta?.lore = mutableListOf("An oddly saturated sponge")
    sponge.itemMeta = sMeta

    val asPlugin = AzureSponge.instance as Plugin
    val key = NamespacedKey(asPlugin, "saturated_sponge")
    val sr = ShapelessRecipe(key, sponge)
    sr.addIngredient(Material.WATER_BUCKET)
    sr.addIngredient(Material.SPONGE)
    asPlugin.server.addRecipe(sr)
}

