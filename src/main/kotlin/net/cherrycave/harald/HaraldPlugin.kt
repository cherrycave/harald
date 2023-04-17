package net.cherrycave.harald

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import net.cherrycave.harald.appearance.PlayerListManager
import net.cherrycave.harald.config.AppearanceConfig
import net.cherrycave.harald.listener.PlayerConnectListener
import net.cherrycave.harald.listener.ProxyPingListener
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import java.net.InetSocketAddress
import java.nio.file.Path


@Plugin(
    id = "harald", name = "Harald", version = "0.1.0",
    url = "https://cherrycave.net", description = "Proxy Display", authors = ["StckOverflw"]
)
class HaraldPlugin @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory val dataDirectory: Path
) {
     val miniMessage = MiniMessage.miniMessage()

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent?) {
        logger.info("Harald is geil!")

        server.registerServer(ServerInfo("", InetSocketAddress("localhost", 25565)))

        PlayerListManager.initialize(this, server)

        server.eventManager.register(this, ProxyPingListener(miniMessage, AppearanceConfig(dataDirectory)))
        server.eventManager.register(this, PlayerConnectListener())
    }

}