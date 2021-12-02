package net.blueberrymc.blueberryFarm

import org.gradle.api.Project
import org.gradle.api.provider.Property

class PluginConfig(project: Project) {
    val minecraftVersion: Property<String> = project.objects.property(String::class.java)
    val apiVersion: Property<String> = project.objects.property(String::class.java)
}
