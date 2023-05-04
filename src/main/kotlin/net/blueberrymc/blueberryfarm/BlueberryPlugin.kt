package net.blueberrymc.blueberryfarm

import net.blueberrymc.blueberryfarm.actions.DownloadInstallerJarTask
import net.blueberrymc.blueberryfarm.actions.DownloadVanillaJarTask
import net.blueberrymc.blueberryfarm.actions.PatchVanillaJarTask
import net.blueberrymc.blueberryfarm.actions.RunClientTask
import net.blueberrymc.blueberryfarm.actions.RunServerTask
import net.blueberrymc.blueberryfarm.actions.UnzipInstallerJarTask
import net.blueberrymc.blueberryfarm.tasks.RunClient
import net.blueberrymc.blueberryfarm.tasks.RunServer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import java.io.File

class BlueberryPlugin : Plugin<Project> {
    companion object {
        internal val dependencyHandler2ProjectMap = mutableMapOf<DependencyHandler, Project>()
    }

    internal lateinit var configuration: PluginConfig

    override fun apply(project: Project) {
        configuration = PluginConfig(project)
        dependencyHandler2ProjectMap[project.dependencies] = project
        project.subprojects.forEach { p ->
            dependencyHandler2ProjectMap[p.dependencies] = p
        }
        File("temp").mkdir()
        File("temp/testClient").mkdir()
        File("temp/testServer").mkdir()
        project.task("downloadInstallerJar", DownloadInstallerJarTask())
        project.task("unzipInstallerJar", UnzipInstallerJarTask())
        project.task("downloadVanillaJar", DownloadVanillaJarTask())
        project.task("patchVanillaJar", PatchVanillaJarTask())
        project.tasks.register("runClient", RunClient::class.java, RunClientTask())
        project.tasks.register("runServer", RunServer::class.java, RunServerTask())
    }
}

internal fun Project.getBlueberryConfig() = (this.plugins.getPlugin("net.blueberrymc.blueberryfarm") as BlueberryPlugin).configuration

fun Project.blueberry(consumer: PluginConfig.() -> Unit) {
    consumer(getBlueberryConfig())
}

private fun DependencyHandler.unwrap(): DependencyHandler {
    if (this::class.java.typeName == "org.gradle.kotlin.dsl.DependencyHandlerScope") {
        return try {
            this::class.java.getDeclaredMethod("getDelegate").apply { isAccessible = true }(this)
        } catch (e: NoSuchMethodException) {
            this::class.java.getDeclaredField("dependencies").apply { isAccessible = true }[this]
        } as DependencyHandler
    }
    return this
}

fun DependencyHandler.blueberry(dependencies: Boolean = true, loadAPI: Boolean = true, loadBlueberryFromMaven: Boolean = false, loadForgeAPI: Boolean = false, loadKotlin: Boolean = false) {
    val project = BlueberryPlugin.dependencyHandler2ProjectMap[this.unwrap()]
        ?: throw IllegalArgumentException("BlueberryPlugin not initialized for this context")
    val config = project.getBlueberryConfig()
    if (dependencies) {
        val libs =
            LIBRARIES[config.minecraftVersion.get()] ?: error("${config.minecraftVersion.get()} is not supported")
        libs.forEach {
            if (!loadKotlin && it.startsWith("org.jetbrains.kotlin:")) return@forEach
            add("compileOnly", it)
        }
    }
    if (loadBlueberryFromMaven) {
        add("compileOnly", "net.blueberrymc:blueberry:${config.minecraftVersion.get()}-${config.apiVersion.get()}")?.apply {
            exclude("com.github.Vatuu", "discord-rpc")
        }
    } else {
        add("compileOnly", project.files("temp/patched-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar"))
    }
    if (loadForgeAPI) {
        add("compileOnly", "net.blueberrymc:minecraftforge-api:${config.apiVersion.get()}")
    }
    if (loadAPI) {
        add("compileOnly", "net.blueberrymc:blueberry-api:${config.apiVersion.get()}")?.apply {
            exclude("com.github.Vatuu", "discord-rpc")
        }
    }
}

fun Dependency.exclude(group: String, module: String) {
    if (this is ModuleDependency) {
        this.exclude(mapOf("group" to group, "module" to module))
    }
}
