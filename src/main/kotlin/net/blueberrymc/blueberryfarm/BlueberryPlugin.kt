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

fun DependencyHandler.blueberry(dependencies: Boolean = true, loadAPI: Boolean = true, loadBlueberryFromMaven: Boolean = false) {
    val lwjglVersion = "3.2.2"
    val project = BlueberryPlugin.dependencyHandler2ProjectMap[this.unwrap()]
        ?: throw IllegalArgumentException("BlueberryPlugin not initialized for this context")
    val config = project.getBlueberryConfig()
    if (dependencies) {
        add("compileOnly", "net.blueberrymc:minecraftforge-api:${config.apiVersion.get()}")
        add("compileOnly", "net.sf.jopt-simple:jopt-simple:5.0.4")
        add("compileOnly", "net.minecrell:terminalconsoleappender:1.2.0")
        add("compileOnly", "org.jline:jline-terminal-jansi:3.12.1")
        add("compileOnly", "org.lwjgl:lwjgl:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-stb:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-stb:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-stb:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-glfw:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-opengl:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-openal:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-openal:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-openal:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-openal:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-tinyfd:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-windows")
        add("compileOnly", "org.lwjgl:lwjgl-jemalloc:$lwjglVersion")
        add("compileOnly", "org.lwjgl:lwjgl-jemalloc:$lwjglVersion:natives-linux")
        add("compileOnly", "org.lwjgl:lwjgl-jemalloc:$lwjglVersion:natives-macos")
        add("compileOnly", "org.lwjgl:lwjgl-jemalloc:$lwjglVersion:natives-windows")
        add("compileOnly", "com.github.oshi:oshi-core:6.2.2")
        add("compileOnly", "com.mojang:blocklist:1.0.6")
        add("compileOnly", "com.mojang:text2speech:1.11.3")
        add("compileOnly", "com.mojang:text2speech:1.11.3:natives-linux")
        add("compileOnly", "com.mojang:text2speech:1.11.3:natives-windows")
        add("compileOnly", "net.java.jutils:jutils:1.0.0")
        add("compileOnly", "net.java.dev.jna:jna:5.9.0")
        add("compileOnly", "net.java.dev.jna:jna-platform:5.9.0")
        add("compileOnly", "com.ibm.icu:icu4j:71.1")
        add("compileOnly", "org.apache.commons:commons-lang3:3.12.0")
        add("compileOnly", "commons-io:commons-io:2.11.0")
        add("compileOnly", "commons-logging:commons-logging:1.2")
        add("compileOnly", "org.apache.logging.log4j:log4j-api:2.19.0")
        add("compileOnly", "org.apache.logging.log4j:log4j-core:2.19.0")
        add("compileOnly", "org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
        add("compileOnly", "org.slf4j:slf4j-api:2.0.1")
        add("compileOnly", "com.mojang:logging:1.1.1")
    }
    if (loadBlueberryFromMaven) {
        add("compileOnly", "net.blueberrymc:blueberry:${config.minecraftVersion.get()}-${config.apiVersion.get()}")?.apply {
            exclude("com.github.Vatuu", "discord-rpc")
        }
    } else {
        add("compileOnly", project.files("temp/patched-${config.minecraftVersion.get()}-${config.apiVersion.get()}.jar"))
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
