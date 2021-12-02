package net.blueberrymc.blueberryFarm.tasks

import net.blueberrymc.blueberryFarm.getBlueberryConfig
import net.blueberrymc.blueberryFarm.util.Util
import org.gradle.api.Action
import org.gradle.api.Task
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.security.MessageDigest
import java.util.Arrays
import java.util.Properties
import java.util.zip.ZipInputStream

class DownloadVanillaJarTask : Action<Task> {
    override fun execute(task: Task) {
        task.dependsOn("unzipInstallerJar")
        task.doLast {
            val config = task.project.getBlueberryConfig()
            val file = File("temp", "patcher-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            val propsFileByteArray = ZipInputStream(file.inputStream()).use { stream ->
                generateSequence { stream.nextEntry }
                    .filterNot { it.isDirectory }
                    .first { it.name == "patch.properties" }
                    .let { stream.readBytes() }
            }
            val properties = Properties()
            ByteArrayInputStream(propsFileByteArray).use { properties.load(it) }
            val vanillaURL = properties.getProperty("vanillaUrl")
                ?: throw AssertionError("vanillaUrl is not defined in patch.properties")
            val vanillaHash = properties.getProperty("vanillaHash")
            val vanillaJarFile = File("temp", "vanilla-${config.minecraftVersion.get()}.jar")
            val digest = MessageDigest.getInstance("SHA-256")
            val dirty = if (vanillaHash == null || !vanillaJarFile.exists()) {
                true
            } else {
                val bytes = vanillaJarFile.readBytes()
                !Arrays.equals(Util.hexToBytes(vanillaHash), digest.digest(bytes))
            }
            if (!dirty) return@doLast
            vanillaJarFile.apply {
                if (exists()) delete()
                createNewFile()
                outputStream()
                    .channel
                    .use { it.transferFrom(Channels.newChannel(URL(vanillaURL).openStream()), 0, Long.MAX_VALUE) }
            }
            if (vanillaHash == null) return@doLast
            val bytes = vanillaJarFile.readBytes()
            val hash = digest.digest(bytes)
            if (!Arrays.equals(Util.hexToBytes(vanillaHash), hash)) {
                throw AssertionError("SHA-256 hash does not match; expected = ${Util.hexToBytes(vanillaHash)}, actual = ${Util.bytesToHex(hash)}")
            }
        }
    }
}
