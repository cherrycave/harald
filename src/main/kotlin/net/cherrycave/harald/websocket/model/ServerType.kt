package net.cherrycave.harald.websocket.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ServerType {
    @SerialName("lobby")
    LOBBY,
    @SerialName("other")
    OTHER
}