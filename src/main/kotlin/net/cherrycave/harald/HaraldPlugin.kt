package net.cherrycave.harald

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.*
import net.cherrycave.birgid.GertrudClient
import net.cherrycave.birgid.closeConnection
import net.cherrycave.birgid.connect
import net.cherrycave.harald.appearance.PlayerListManager
import net.cherrycave.harald.config.AppearanceConfig
import net.cherrycave.harald.gertrud.handleSendRequests
import net.cherrycave.harald.listener.PlayerConnectListener
import net.cherrycave.harald.listener.ProxyPingListener
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
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

    private val gertrudClient = GertrudClient {
        host = "cherrycave-backend.cap.stckoverflw.net"
        port = 80
        identifier = "proxy"
        apiKey = "UKFo@BYKWsG#rcnYzyW^jBVkP53&etky7zan*vMr6A"
    }

    init {
        coroutineScope.launch {
            gertrudClient.connect()
        }
    }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        logger.info("Harald is geil!")

//        server.registerServer(ServerInfo("", InetSocketAddress("localhost", 25565)))

        PlayerListManager.initialize(this, server)

        server.eventManager.register(this, ProxyPingListener(miniMessage, AppearanceConfig(dataDirectory)))
        server.eventManager.register(this, PlayerConnectListener())

        coroutineScope.launch {
            handleSendRequests(gertrudClient, server)
        }
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent) {
        runBlocking {
            gertrudClient.closeConnection()
        }
    }

}