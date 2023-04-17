package net.cherrycave.harald.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class AppearanceConfig(private val dataPath: Path) {

    @Serializable
    data class PingData(
        val firstLine: String,
        val secondLines: List<String>
    )

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    private var pingData: PingData? = null

    fun getPingData(): PingData {

        if (pingData != null) {
            return pingData!!
        }

        if (!dataPath.exists()) {
            dataPath.createDirectories()
        }

        val pingDataFile = dataPath.resolve("ping.json")

        val default = PingData(
            firstLine = "                <bold><gradient:#CA75DF:#FD4F4F>CherryCave.net</gradient></bold> <#eb7d34>-</#eb7d34> <bold><blue>1.19</blue></bold>",
            secondLines = listOf(
                "              <#47bcff>We have Cherries and Cats :3</#47bcff>",
            )
        )

        if (!pingDataFile.exists()) {
            pingDataFile.writeText(json.encodeToString(default))
        } else {
            pingData = json.decodeFromString(pingDataFile.readText())
        }

        return default
    }

    fun setPingData(pingData: PingData) {
        this.pingData = pingData

        val pingDataFile = dataPath.resolve("ping.json")

        pingDataFile.writeText(json.encodeToString(pingData))
    }

}