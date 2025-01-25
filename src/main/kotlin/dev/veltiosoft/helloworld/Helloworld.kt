package dev.veltiosoft.helloworld

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Helloworld : JavaPlugin(), Listener {

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        event.player.sendMessage("Hello, ${event.player.name}")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info(("goodbye, world!"))
    }
}
