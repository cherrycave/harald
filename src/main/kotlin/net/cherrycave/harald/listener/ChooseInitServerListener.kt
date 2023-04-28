package net.cherrycave.harald.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.proxy.server.RegisteredServer

class ChooseInitServerListener {

    companion object {
        val lobbyServer = mutableListOf<RegisteredServer>()
    }

    @Subscribe
    fun onChooseInitServer(event: PlayerChooseInitialServerEvent) {
        event.setInitialServer(lobbyServer.random())
    }

}