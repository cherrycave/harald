package net.cherrycave.harald.websocket

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.cherrycave.harald.utils.SerializableUUID

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("messageType")
sealed class MessagePayload

@Serializable
@SerialName("empty")
object Empty : MessagePayload()

@Serializable
@SerialName("ok")
object GenericOk : MessagePayload()

@Serializable
@SerialName("error")
data class GenericError(
    val message: String
) : MessagePayload()


@Serializable
@SerialName("send-request")
data class SendRequest(
    val players: List<SerializableUUID>,
    val server: String
) : MessagePayload()

@Serializable
@SerialName("register-server-request")
data class RegisterServerRequest(
    val register: Boolean,
    val serverType: String,
    val identifier: String,
    val host: String,
    val port: Int,
) : MessagePayload()


