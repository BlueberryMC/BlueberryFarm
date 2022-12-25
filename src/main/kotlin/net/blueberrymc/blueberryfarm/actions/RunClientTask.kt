package net.blueberrymc.blueberryfarm.actions

import net.blueberrymc.blueberryfarm.tasks.RunClient
import org.gradle.api.Action

class RunClientTask : Action<RunClient> {
    override fun execute(task: RunClient) {
        task.dependsOn("patchVanillaJar")
        task.dependsOn("build")
    }
}