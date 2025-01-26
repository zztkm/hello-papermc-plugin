package dev.veltiosoft.discordnotify

data class EventConfig(
    val enabled: Boolean = false,
    val message: String = ""
)

data class Config(
    val discordWebhookUrl: String,
    val onPlayerJoin: EventConfig,
    val onPlayerQuit: EventConfig
)
