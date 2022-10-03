package xyz.azuremoon

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import xyz.azuremoon.commands.CommandController
import xyz.azuremoon.listeners.ASEventListener
import xyz.azuremoon.listeners.AdjListener
import xyz.azuremoon.recipes.sSponge

@Suppress("unused")
class AzureSponge : JavaPlugin() {

    companion object {
        var instance: AzureSponge? = null
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("AzureSponge says Hello star-shine! :3")
        saveDefaultConfig()
        server.pluginManager.registerEvents(ASEventListener(), this)
        server.pluginManager.registerEvents(AdjListener(), this)
        sSponge()
        logger.info("AzureSponge : Loaded 1 recipe")
    }

    override fun onDisable() {
        instance = null
        logger.info("Goodbye, World!")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!command.testPermission(sender)) { return false }
        return CommandController.commandRoute(sender, command)
    }

}

