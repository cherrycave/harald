package net.cherrycave.harald.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import net.cherrycave.harald.appearance.PlayerListManager

class PlayerConnectListener {

    @Subscribe
    fun onPlayerConnect(event: PostLoginEvent) {
        event.player.sendPlayerListHeaderAndFooter(
            PlayerListManager.header,
            PlayerListManager.footer
        )
    }

    @Subscribe
    fun onPlayerSwitchServer(event: ServerConnectedEvent) {
        event.player.sendPlayerListHeaderAndFooter(PlayerListManager.header, PlayerListManager.footer)
    }

}