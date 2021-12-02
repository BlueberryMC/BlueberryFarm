package net.blueberrymc.blueberryFarm.tasks

import org.gradle.api.Action
import org.gradle.api.tasks.JavaExec
import java.io.File
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
class RunClientTask : Action<JavaExec> {
    var mainClass = "net.blueberrymc.client.main.ClientMain"
    var username = "Player${(Math.random() * 999).toInt()}"
    var userType = "mojang"
    var uuid = UUID(0, 0)
    var accessToken = "test"
    var additionalSourceDirs = mutableListOf<File>()
    var additionalIncludeDirs = mutableListOf<File>()
    var jvmArgs = mutableListOf<String>()

    override fun execute(task: JavaExec) {
        task.dependsOn("patchVanillaJar")
        task.dependsOn("build")
        task.classpath =
            ((task.project as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer).getByName(
                "main"
            ).compileClasspath
        task.mainClass.set(mainClass)
        task.workingDir = File(task.project.projectDir, "temp/testClient").apply { mkdirs() }
        val args = mutableListOf<String>()
        args.add("--version=release")
        args.add("--assetIndex=1.18")
        args.add("--versionType=release")
        args.add("--username=$username")
        args.add("--userType=$userType")
        args.add("--uuid=$uuid")
        args.add("--accessToken=$accessToken")
        args.add("--sourceDir=${File(task.project.projectDir, "build/classes/java/main").absolutePath}")
        args.add("--includeDir=${File(task.project.projectDir, "build/resources/main").absolutePath}")
        additionalSourceDirs.forEach { args.add("--sourceDir=${it.absolutePath}") }
        additionalIncludeDirs.forEach { args.add("--includeDir=${it.absolutePath}") }
        task.args = args
        task.jvmArgs.apply {
            if (this == null) {
                task.jvmArgs = jvmArgs
            } else {
                this.addAll(jvmArgs)
            }
        }
    }
}