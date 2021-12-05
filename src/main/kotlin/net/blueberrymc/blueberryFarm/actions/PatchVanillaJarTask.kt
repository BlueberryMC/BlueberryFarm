package net.blueberrymc.blueberryFarm.actions

import io.sigpipe.jbsdiff.Patch
import net.blueberrymc.blueberryFarm.getBlueberryConfig
import net.blueberrymc.blueberryFarm.util.Util
import org.gradle.api.Action
import org.gradle.api.Task
import java.io.File
import java.security.MessageDigest
import java.util.Arrays
import java.util.Properties
import java.util.zip.ZipInputStream

class PatchVanillaJarTask : Action<Task> {
    override fun execute(task: Task) {
        task.dependsOn("downloadVanillaJar")
        task.doLast {
            val config = task.project.getBlueberryConfig()
            val patcherJarFile = File("temp", "patcher-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            val properties = Properties()
            var patchFileByteArray: ByteArray? = null
            ZipInputStream(patcherJarFile.inputStream()).use { stream ->
                generateSequence { stream.nextEntry }
                    .filterNot { it.isDirectory }
                    .forEach {
                        if (it.name == "patch.properties") {
                            properties.load(stream)
                        } else if (it.name == "patch.bz2") {
                            patchFileByteArray = stream.readBytes()
                        }
                    }
            }
            if (patchFileByteArray == null) throw AssertionError("patch.bz2 was not read")
            val patchedHash = properties.getProperty("patchedHash")
            val vanillaJarFile = File("temp", "vanilla-${config.minecraftVersion.get()}.jar")
            if (!vanillaJarFile.exists()) throw AssertionError("${vanillaJarFile.path} does not exist")
            val patchedJarFile = File("temp", "patched-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            val digest = MessageDigest.getInstance("SHA-256")
            val dirty = if (patchedHash == null || !patchedJarFile.exists()) {
                true
            } else {
                val bytes = patchedJarFile.readBytes()
                !Arrays.equals(Util.hexToBytes(patchedHash), digest.digest(bytes))
            }
            if (!dirty) return@doLast
            Patch.patch(vanillaJarFile.readBytes(), patchFileByteArray, patchedJarFile.outputStream())
            if (patchedHash == null) return@doLast
            val bytes = patchedJarFile.readBytes()
            val hash = digest.digest(bytes)
            if (!Arrays.equals(Util.hexToBytes(patchedHash), hash)) {
                throw AssertionError("SHA-256 hash does not match; expected = ${Util.hexToBytes(patchedHash)}, actual = ${Util.bytesToHex(hash)}")
            }
        }
    }
}
