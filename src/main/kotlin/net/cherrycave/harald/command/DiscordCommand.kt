package net.cherrycave.harald.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import net.cherrycave.harald.websocket.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration

fun discordCommand(): BrigadierCommand = BrigadierCommand(LiteralArgumentBuilder.literal<CommandSource>("discord")
    .executes { context ->
        val source = context.source
        source.sendMessage(
            Component.join(
                JoinConfiguration.newlines(),
                miniMessage.deserialize("<gray><bold>Join us on our Discord Server: "),
                miniMessage.deserialize("   <blue><click:open_url:https://discord.gg/kWeC83dpkb>discord.gg/kWeC83dpkb")
            )
        )
        1
    })