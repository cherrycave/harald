package net.cherrycave.harald.websocket

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import net.cherrycave.harald.listener.ChooseInitServerListener
import net.cherrycave.harald.websocket.model.ServerType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import java.net.InetSocketAddress

val miniMessage = MiniMessage.miniMessage()

fun handleMessage(message: BaseMessage, proxyServer: ProxyServer) {
    when (message.payload) {
        is SendRequest -> {}
        is RegisterServerRequest -> {
            val server = ServerInfo(
                if (message.payload.serverType == ServerType.LOBBY) "lobby-" + message.payload.identifier else message.payload.identifier,
                InetSocketAddress(message.payload.host, message.payload.port)
            )
            if (message.payload.register) {
                println("Registering server ${server.name}")
                val registeredServer = proxyServer.registerServer(server)
                ChooseInitServerListener.lobbyServer.add(registeredServer)
            } else {
                println("Unregistering server ${server.name}")
                val registeredServer = proxyServer.allServers.find {
                    it.serverInfo.name == server.name
                }
                registeredServer?.playersConnected?.forEach {
                    val otherLobby = ChooseInitServerListener.lobbyServer.randomOrNull()
                    if (otherLobby == null) {
                        it.disconnect(
                            Component.join(
                                JoinConfiguration.newlines(),
                                miniMessage.deserialize("<red>${server.name} <gray>is shutting down and there was no Lobby Server to connect you to."),
                                miniMessage.deserialize("<gray>Try reconnecting in a few moments.")
                            )
                        )
                        return
                    }
                    it.createConnectionRequest(otherLobby).fireAndForget()
                }
                proxyServer.unregisterServer(server)
            }
        }

        is Empty -> {}
        is GenericError -> {}
        is GenericOk -> {}
    }
}