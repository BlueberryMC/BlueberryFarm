package net.blueberrymc.blueberryFarm.actions

import net.blueberrymc.blueberryFarm.tasks.RunClient
import org.gradle.api.Action

class RunClientTask : Action<RunClient> {
    override fun execute(task: RunClient) {
        task.dependsOn("patchVanillaJar")
        task.dependsOn("build")
    }
}