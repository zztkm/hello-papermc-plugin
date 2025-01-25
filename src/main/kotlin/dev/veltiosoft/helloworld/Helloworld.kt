package dev.veltiosoft.helloworld

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI

class Helloworld : JavaPlugin(), Listener {

    private var discordWebhookUrl: String = ""

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this)

        saveDefaultConfig()

        // Load the Discord webhook URL from the config
        discordWebhookUrl = config.getString("discord_webhook_url") ?: ""
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        event.player.sendMessage("[helloworld] Hello, ${event.player.name}")
        sendDiscordMessage("Player ${event.player.name} has joined the server")
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        sendDiscordMessage("Player ${event.player.name} has left the server")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info(("goodbye, world!"))
    }

    private fun sendDiscordMessage(message: String) {
        if (discordWebhookUrl.isEmpty()) {
            logger.warning("Discord webhook URL is not set")
            return
        }
        val url = URI(discordWebhookUrl).toURL()
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 1_000 // 1 second
        conn.readTimeout = 1_000 // 1 second
        conn.requestMethod = "POST"

        // headers
        conn.setRequestProperty("Content-Type", "application/json; utf-8")
        conn.setRequestProperty("Accept", "application/json")

        conn.doOutput = true

        // body
        conn.outputStream.use { outputStream ->
            OutputStreamWriter(outputStream, "UTF-8").use { writer ->
                writer.write("{\"content\":\"$message\"}")
                writer.flush()
            }
        }

        // response
        val code = conn.responseCode
        return if (code == 204) {
            logger.info("Discord message sent successfully")
        } else {
            logger.warning("Failed to send Discord message: $code")
        }
    }
}
