package dev.veltiosoft.discordnotify

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI

class DiscordNotify : JavaPlugin(), Listener {

    private val onPlayerJoinDefaultMessage = "{player} has joined the server"
    private val onPlayerQuitDefaultMessage = "{player} has left the server"

    // NOTE: pluginConfig は onEnable() で初期化されるので
    // EventHandler の中で安全に呼び出すことができる(はず)
    private lateinit var pluginConfig: Config

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this)
        saveDefaultConfig()
        pluginConfig = loadConfig()
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        if (!pluginConfig.onPlayerJoin.enabled) {
            return
        }
        sendDiscordMessage(replaceMessageVariables(pluginConfig.onPlayerJoin.message, event.player.name))
    }

    @EventHandler
    fun onPlayerQuit(event: org.bukkit.event.player.PlayerQuitEvent) {
        if (!pluginConfig.onPlayerQuit.enabled) {
            return
        }
        sendDiscordMessage(replaceMessageVariables(pluginConfig.onPlayerQuit.message, event.player.name))
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info(("goodbye, world!"))
    }

    private fun loadConfig() : Config {
        // NOTE: discordWebhookUrl は必須
        val discordWebhookUrl = config.getString("discord_webhook_url") ?: ""

        // Refs: https://jd.papermc.io/paper/1.21.4/org/bukkit/configuration/MemorySection.html#contains(java.lang.String)
        if (!config.contains("events")) {
            // events がない場合は onPlayerJoin と onPlayerQuit のみデフォルト設定をして Config を返す
            val onPlayerJoin = loadEventConfig("on_player_join", defaultEnabled = true, defaultMessage = onPlayerJoinDefaultMessage)
            val onPlayerQuit = loadEventConfig("on_player_quit", defaultEnabled = true, defaultMessage = onPlayerQuitDefaultMessage)
            return Config(discordWebhookUrl, onPlayerJoin, onPlayerQuit)
        }
        // onPlayerJoin と onPlayerQuit のみデフォルト true にする
        val onPlayerJoin = loadEventConfig("on_player_join", defaultEnabled = true, defaultMessage = onPlayerJoinDefaultMessage)
        val onPlayerQuit = loadEventConfig("on_player_quit", defaultEnabled = true, defaultMessage = onPlayerQuitDefaultMessage)
        return Config(discordWebhookUrl, onPlayerJoin, onPlayerQuit)
    }

    private fun loadEventConfig(path: String, defaultEnabled: Boolean = false, defaultMessage: String = "") : EventConfig {
        val enabled = config.getBoolean("events.$path.enabled", defaultEnabled)
        val message = config.getString("events.$path.message") ?: defaultMessage
        return EventConfig(enabled, message)
    }

    private fun replaceMessageVariables(message: String, playerName: String) : String {
        return message.replace("{player}", playerName)
    }

    private fun sendDiscordMessage(message: String) {
        if (pluginConfig.discordWebhookUrl.isEmpty()) {
            logger.warning("Discord webhook URL is not set")
            return
        }
        val url = URI(pluginConfig.discordWebhookUrl).toURL()
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
