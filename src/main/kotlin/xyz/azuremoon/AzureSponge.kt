package xyz.azuremoon

import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class AzureSponge : JavaPlugin() {

    companion object {

        var instance: AzureSponge? = null
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("Azure says Hello star-shine! :3")
        saveDefaultConfig()
        server.pluginManager.registerEvents(ASEventListener(), this)
    }

    override fun onDisable() {
        instance = null
        logger.info("Goodbye, World!")
    }
}

