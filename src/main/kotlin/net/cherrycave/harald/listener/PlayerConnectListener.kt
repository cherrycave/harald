package net.cherrycave.harald.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import net.cherrycave.harald.appearance.PlayerListManager

class PlayerConnectListener {

    @Subscribe
    fun onPlayerConnect(event: PostLoginEvent) {
        event.player.sendPlayerListHeaderAndFooter(
            PlayerListManager.header,
            PlayerListManager.footer
        )
    }

}