package net.cherrycave.harald.appearance

import com.velocitypowered.api.proxy.ProxyServer
import net.cherrycave.harald.HaraldPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.minimessage.MiniMessage
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object PlayerListManager {

    private lateinit var proxyServer: ProxyServer

    private val miniMessage = MiniMessage.miniMessage()

    var header = Component.join(
        JoinConfiguration.newlines(),
        Component.text(" ".repeat(48)),
        miniMessage.deserialize("<bold><gradient:#CA75DF:#FD4F4F>Cherry Cave</gradient></bold>"),
        Component.empty()
    )

    var footer = Component.join(
        JoinConfiguration.newlines(),
        Component.empty(),
        miniMessage.deserialize("<gray></gray>"),
        miniMessage.deserialize("<blue>Better than Nick!</blue>"),
        Component.empty()
    )

    private var currentVariant = 0

    private val footerVariants = listOf(
        Component.join(
            JoinConfiguration.newlines(),
            Component.empty(),
            miniMessage.deserialize("<gray>Powered by Harald</gray>"),
            miniMessage.deserialize("<blue>Better than Nick!</blue>"),
            Component.empty()
        ),
        Component.join(
            JoinConfiguration.newlines(),
            Component.empty(),
            miniMessage.deserialize("<gray>Check out this very cool website:</gray>"),
            miniMessage.deserialize("<blue>stckoverflw.net</blue>"),
            Component.empty()
        ),
        Component.join(
            JoinConfiguration.newlines(),
            Component.empty(),
            miniMessage.deserialize("<gray>Join us on Discord:</gray>"),
            miniMessage.deserialize("<blue>/discord</blue>"),
            Component.empty()
        )
    )

    fun initialize(plugin: HaraldPlugin, proxyServer: ProxyServer) {
        this.proxyServer = proxyServer

        proxyServer.scheduler.buildTask(plugin) {
            proxyServer.allPlayers.forEach {
                it.sendPlayerListHeaderAndFooter(header, footer)
            }

            currentVariant = (currentVariant + 1) % footerVariants.size
            footer = footerVariants[currentVariant]
        }.repeat(5.seconds.toJavaDuration()).schedule()
    }

}