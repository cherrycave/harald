package net.cherrycave.harald

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import io.ktor.websocket.*
import kotlinx.coroutines.*
import net.cherrycave.birgid.GertrudClient
import net.cherrycave.birgid.request.getServerRegistrations
import net.cherrycave.harald.appearance.PlayerListManager
import net.cherrycave.harald.command.discordCommand
import net.cherrycave.harald.command.hubCommand
import net.cherrycave.harald.config.AppearanceConfig
import net.cherrycave.harald.listener.ChooseInitServerListener
import net.cherrycave.harald.listener.PlayerConnectListener
import net.cherrycave.harald.listener.ProxyPingListener
import net.cherrycave.harald.websocket.connectToBackend
import net.cherrycave.harald.websocket.websocketConnection
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import java.net.InetSocketAddress
import java.nio.file.Path


@Plugin(
    id = "harald", name = "Harald", version = "0.1.0",
    url = "https://cherrycave.net", description = "Harald schaut hier überall drüber", authors = ["StckOverflw"]
)
class HaraldPlugin @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory val dataDirectory: Path
) {
    private val miniMessage = MiniMessage.miniMessage()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val gertrudClient = GertrudClient {
        host = System.getenv("BACKEND_HOST") ?: "localhost"
        port = System.getenv("BACKEND_PORT")?.toInt() ?: 6969
        apiKey = System.getenv("BACKEND_API_KEY") ?: error("No API Key provided")
        identifier = System.getenv("P_SERVER_UUID") ?: "proxy"
        https = false
    }

    init {
        coroutineScope.launch {
            connectToBackend(proxyServer = server)
        }
    }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        logger.info("Harald is geil!")
        coroutineScope.launch {
            gertrudClient.getServerRegistrations().getOrElse { emptyArray() }.forEach {
                logger.info("Registering server with identifier: ${it.identifier} of type ${it.serverType}")
                val name = "${it.serverType}-${it.identifier}"
                server.registerServer(ServerInfo(name, InetSocketAddress(it.host, it.port)))
            }
        }

        PlayerListManager.initialize(this, server)

        val commandManager: CommandManager = server.commandManager
        commandManager.register(
            commandManager.metaBuilder("discord")
                .aliases("dc", "community")
                .plugin(this)
                .build(),
            discordCommand()
        )
        commandManager.register(
            commandManager.metaBuilder("hub")
                .aliases("l", "lobby")
                .plugin(this)
                .build(),
            hubCommand(server)
        )

        server.eventManager.register(this, ProxyPingListener(miniMessage, AppearanceConfig(dataDirectory)))
        server.eventManager.register(this, PlayerConnectListener())
        server.eventManager.register(this, ChooseInitServerListener(server))

    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent) {
        runBlocking {
            websocketConnection.close(CloseReason(CloseReason.Codes.NORMAL, "Server shutdown"))
        }
    }

}