package net.cherrycave.harald.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class ChooseInitServerListener(private val proxy: ProxyServer) {

    @Subscribe
    fun onChooseInitServer(event: PlayerChooseInitialServerEvent) {
        val server = proxy.matchServer("lobby").randomOrNull()
        if (server == null) {
            event.player.disconnect(
                Component.text(
                    "There are no Lobby Servers available. Try again in a few moments.",
                    NamedTextColor.RED
                )
            )
            return
        }
        event.setInitialServer(server)
    }

}