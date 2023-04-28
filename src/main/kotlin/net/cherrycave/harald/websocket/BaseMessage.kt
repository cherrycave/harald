package net.cherrycave.harald.websocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseMessage(
    val messageId: String,
    val messageType: MessageType,
    val payload: MessagePayload,
) {
    @Serializable
    enum class MessageType {
        @SerialName("init")
        INIT,

        @SerialName("response")
        RESPONSE,

        @SerialName("keepAlive")
        KEEP_ALIVE,
    }
}


