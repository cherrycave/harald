package net.cherrycave.harald.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer

fun hubCommand(proxyServer: ProxyServer) = LiteralArgumentBuilder.literal<CommandSource>("hub")
    .executes { context ->
        val source = context.source
        if (source is Player) {
            source.createConnectionRequest(proxyServer.matchServer("lobby").random()).fireAndForget()
        }
        1
    }