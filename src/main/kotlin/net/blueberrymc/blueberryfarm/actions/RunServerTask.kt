package net.blueberrymc.blueberryfarm.actions

import net.blueberrymc.blueberryfarm.tasks.RunServer
import org.gradle.api.Action

class RunServerTask : Action<RunServer> {
    override fun execute(task: RunServer) {
        task.dependsOn("patchVanillaJar")
        // excludeClientClasses
        task.dependsOn("build")
    }
}