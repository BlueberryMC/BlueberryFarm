package net.blueberrymc.blueberryFarm.actions

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.blueberrymc.blueberryFarm.getBlueberryConfig
import org.gradle.api.Action
import org.gradle.api.Task
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.util.regex.Pattern

class DownloadInstallerJarTask : Action<Task> {
    override fun execute(task: Task) {
        task.doLast {
            val config = task.project.getBlueberryConfig()
            val file = File("temp", "blueberry-installer-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            if (file.exists()) {
                println("Reusing existing installer ${file.absolutePath}")
                return@doLast
            }
            val exactMatch = try {
                URL("https://api.github.com/repos/BlueberryMC/Blueberry/releases/tags/${config.minecraftVersion.get()}-${config.apiVersion.get()}${config.buildNumber.orNull.let { if (it == null) "" else "-$it" }}").readText()
            } catch (e: IOException) {
                null
            }
            val obj = exactMatch?.let { Gson().fromJson(it, JsonObject::class.java) }
            val asset = if (obj?.has("assets") == true) {
                obj.get("assets").asJsonArray[0]
            } else {
                val json = URL("https://api.github.com/repos/BlueberryMC/Blueberry/releases").readText()
                val array = Gson().fromJson(json, JsonArray::class.java)
                val checkTagName = { name: String ->
                    val bn = config.buildNumber.orNull
                    if (bn == null) {
                        name.matches("${Pattern.quote(config.minecraftVersion.get())}-${Pattern.quote(config.apiVersion.get())}(-.*)?".toRegex())
                    } else {
                        name == "${config.minecraftVersion.get()}-${config.apiVersion.get()}-$bn"
                    }
                }
                val throwUnmatchedError = {
                    val bn = config.buildNumber.orNull
                    if (bn == null) {
                        error("No matching release: ${config.minecraftVersion.get()}-${config.apiVersion.get()}")
                    } else {
                        error("No matching release: ${config.minecraftVersion.get()}-${config.apiVersion.get()}-$bn")
                    }
                }
                (array.filterIsInstance<JsonObject>()
                    .firstOrNull { checkTagName(it["tag_name"].asString) } ?: throwUnmatchedError())
                    .get("assets")
                    .asJsonArray[0]
            }
            val size = asset.asJsonObject["size"].asInt
            val downloadUrl = asset.asJsonObject["browser_download_url"].asString
            file.apply {
                    if (exists()) delete()
                    createNewFile()
                    outputStream()
                        .channel
                        .use {
                            it.transferFrom(Channels.newChannel(URL(downloadUrl).openStream()), 0, Long.MAX_VALUE)
                        }
                }
            val actualSize = file.readBytes().size
            if (file.exists() && size != actualSize) throw AssertionError("Expected $size bytes, but got $actualSize bytes")
        }
    }
}
