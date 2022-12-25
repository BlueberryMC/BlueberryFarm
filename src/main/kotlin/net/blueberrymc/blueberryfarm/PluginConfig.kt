package net.blueberrymc.blueberryfarm

import org.gradle.api.Project
import org.gradle.api.provider.Property

class PluginConfig(project: Project) {
    // Required
    val minecraftVersion: Property<String> = project.objects.property(String::class.java)
    val apiVersion: Property<String> = project.objects.property(String::class.java)
    val buildNumber: Property<Int> = project.objects.property(Int::class.java)
}
