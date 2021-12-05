package net.blueberrymc.blueberryFarm.actions

import net.blueberrymc.blueberryFarm.tasks.RunServer
import org.gradle.api.Action

class RunServerTask : Action<RunServer> {
    override fun execute(task: RunServer) {
        task.dependsOn("patchVanillaJar")
        // excludeClientClasses
        task.dependsOn("build")
    }
}