package net.cherrycave.harald.gertrud

import com.velocitypowered.api.proxy.ProxyServer
import net.cherrycave.birgid.GertrudClient
import net.cherrycave.birgid.model.SendRequest
import net.cherrycave.birgid.receiver.handleSendRequest

suspend fun handleSendRequests(gertrudClient: GertrudClient, proxyServer: ProxyServer) {
    gertrudClient.handleSendRequest {
        val message = it.payload as SendRequest
        message.players.forEach { uuid ->
            proxyServer.getPlayer(uuid).ifPresent { player ->
                player.createConnectionRequest(proxyServer.matchServer(message.server).random()).fireAndForget()
            }
        }
        Result.success(Unit)
    }
}