package net.blueberrymc.blueberryFarm.tasks

import net.blueberrymc.blueberryFarm.getBlueberryConfig
import org.gradle.api.Action
import org.gradle.api.Task
import java.io.File
import java.util.zip.ZipInputStream

class UnzipInstallerJarTask : Action<Task> {
    override fun execute(task: Task) {
        task.dependsOn("downloadInstallerJar")
        task.doLast {
            val config = task.project.getBlueberryConfig()
            val file = File("temp", "blueberry-installer-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            if (!file.exists()) throw IllegalStateException("${file.path} does not exist")
            val patcherJar = ZipInputStream(file.inputStream()).use { stream ->
                generateSequence { stream.nextEntry }
                    .filterNot { it.isDirectory }
                    .first { it.name == "client.jar" }
                    .let { stream.readBytes() }
            }
            val patcherJarFile = File("temp", "patcher-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar")
            if (patcherJarFile.exists()) patcherJarFile.delete()
            patcherJarFile.createNewFile()
            patcherJarFile.writeBytes(patcherJar)
        }
    }
}
