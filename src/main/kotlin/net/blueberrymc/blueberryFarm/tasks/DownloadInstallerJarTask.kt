package net.blueberrymc.blueberryFarm.tasks

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.blueberrymc.blueberryFarm.getBlueberryConfig
import org.gradle.api.Action
import org.gradle.api.Task
import java.io.File
import java.net.URL
import java.nio.channels.Channels

class DownloadInstallerJarTask : Action<Task> {
    override fun execute(task: Task) {
        task.doLast {
            val config = task.project.getBlueberryConfig()
            val file = File("temp", "blueberry-installer-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            val json = URL("https://api.github.com/repos/BlueberryMC/Blueberry/releases").readText()
            val array = Gson().fromJson(json, JsonArray::class.java)
            val size = array.filterIsInstance<JsonObject>()
                .first { it["tag_name"].asString == "${config.minecraftVersion.get()}-${config.apiVersion.get()}" }["assets"]
                .asJsonArray[0]
                .asJsonObject["size"]
                .asInt
            if (file.exists() && size == file.readBytes().size) return@doLast
            val downloadUrl = array.filterIsInstance<JsonObject>()
                .first { it["tag_name"].asString == "${config.minecraftVersion.get()}-${config.apiVersion.get()}" }["assets"]
                .asJsonArray[0]
                .asJsonObject["browser_download_url"]
                .asString
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
