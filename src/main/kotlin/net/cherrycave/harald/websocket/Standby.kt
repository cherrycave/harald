package net.cherrycave.harald.websocket

import kotlinx.coroutines.channels.Channel

class Standby {

    private val bystanders = mutableMapOf<String, Channel<BaseMessage>>()

    suspend fun waitFor(messageId: String): BaseMessage {
        val channel = Channel<BaseMessage>()
        bystanders[messageId] = channel
        val message = channel.receive()
        bystanders.remove(messageId)?.close()
        return message
    }

    suspend fun process(message: BaseMessage) {
        val channel = bystanders[message.messageId]
        channel?.send(message)
    }

}