package net.cherrycave.harald.websocket

import com.velocitypowered.api.proxy.ProxyServer
import io.github.oshai.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

private val LOG = KotlinLogging.logger { }

lateinit var websocketConnection: DefaultClientWebSocketSession

var disconnect = false

val standby = Standby()

private val json = Json

suspend fun connectToBackend(
    host: String = System.getenv("BACKEND_HOST") ?: "localhost",
    port: Int = System.getenv("BACKEND_PORT")?.toInt() ?: 6969,
    apiKey: String = System.getenv("BACKEND_API_KEY") ?: error("No API Key provided"),
    identifier: String = System.getenv("P_SERVER_UUID") ?: "proxy",
    httpClient: HttpClient = HttpClient {
        install(Logging) {
            this.level = LogLevel.INFO
        }
        install(WebSockets) {
            pingInterval = 1000 * 30
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        defaultRequest {
            header(HttpHeaders.Authorization, apiKey)
            header("X-Server-Identifier", identifier)
        }
    },
    proxyServer: ProxyServer
) {
    var retries = 0

    while (!disconnect) {
        httpClient.webSocket(host = host, port = port, path = "/ws") {
            retries = 0
            var lastKeepAlive = System.currentTimeMillis()

            websocketConnection = this

            launch {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()

                            val message: BaseMessage = try {
                                json.decodeFromString<BaseMessage>(text)
                            } catch (ex: Exception) {
                                println("Failed to decode message: ${ex.message}")
                                continue
                            }

                            when (message.messageType) {
                                BaseMessage.MessageType.INIT -> {
                                    println("received message: $message")
                                    handleMessage(message, proxyServer)
                                }

                                BaseMessage.MessageType.RESPONSE -> {
                                    standby.process(message)
                                }

                                BaseMessage.MessageType.KEEP_ALIVE -> {
                                    lastKeepAlive = System.currentTimeMillis()
                                    send(frame)
                                }
                            }
                        }

                        else -> {
                            // Ignore
                        }
                    }
                }
            }

            while (isActive) {
                delay(3.seconds)

                if (System.currentTimeMillis() - lastKeepAlive > 6.seconds.inWholeMilliseconds) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Keep Alive Timeout"))
                    this.cancel()
                    break
                }
            }

        }

        if (disconnect) {
            break
        }

        if (retries >= 5) {
            throw RuntimeException("Could not reconnect to CherryCave Backend")
        }

        retries++

        LOG.info { "Lost connection. Reconnecting to CherryCave Backend in 5 seconds" }
        delay(5.seconds)
    }
}