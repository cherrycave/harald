package net.cherrycave.harald.websocket

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import net.cherrycave.harald.listener.ChooseInitServerListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import java.net.InetSocketAddress

val miniMessage = MiniMessage.miniMessage()

fun handleMessage(message: BaseMessage, proxyServer: ProxyServer) {
    when (message.payload) {
        is SendRequest -> {
            println("got send request for ${message.payload.players.joinToString(", ") { it.toString() }} to servertype ${message.payload.server}")
            val server = proxyServer.matchServer(message.payload.server).randomOrNull()

            if (server == null) {
                println("no server found for type ${message.payload.server}")
                return
            }

            message.payload.players.forEach {
                println("sending $it to ${server.serverInfo.name}")
                proxyServer.getPlayer(it).ifPresent { player ->
                    player.createConnectionRequest(server).fireAndForget()
                }
            }
        }

        is RegisterServerRequest -> {
            val server = ServerInfo(
                "${message.payload.serverType}-${message.payload.identifier}",
                InetSocketAddress(message.payload.host, message.payload.port)
            )
            if (message.payload.register) {
                println("Registering server ${server.name}")
                proxyServer.registerServer(server)
            } else {
                println("Unregistering server ${server.name}")
                val registeredServer = proxyServer.allServers.find {
                    it.serverInfo.name == server.name
                }
                registeredServer?.playersConnected?.forEach {
                    val otherLobby = proxyServer.matchServer("lobby").randomOrNull()
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