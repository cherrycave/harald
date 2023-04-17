package net.cherrycave.harald.listener

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import net.cherrycave.harald.config.AppearanceConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage

class ProxyPingListener(private val miniMessage: MiniMessage, private val appearanceConfig: AppearanceConfig) {

    @Subscribe(order = PostOrder.NORMAL)
    fun onProxyPing(event: ProxyPingEvent) {
        event.ping =
            event.ping.asBuilder().description(
                Component.join(
                    JoinConfiguration.newlines(),
                    miniMessage.deserialize(appearanceConfig.getPingData().firstLine),
                    miniMessage.deserialize(appearanceConfig.getPingData().secondLines.random())
                )
            ).build()
    }

}