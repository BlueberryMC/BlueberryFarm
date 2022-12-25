package net.blueberrymc.blueberryfarm.tasks

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import java.io.File
import javax.inject.Inject

open class RunServer: JavaExec() {
    @Internal val additionalSourceDirs: ListProperty<File>
    @Internal val additionalIncludeDirs: ListProperty<File>

    init {
        val factory = objectFactory
        mainClass.set("net.blueberrymc.server.main.ServerMain")
        additionalSourceDirs = factory.listProperty(File::class.java).value(mutableListOf())
        additionalIncludeDirs = factory.listProperty(File::class.java).value(mutableListOf())
        classpath = ((project as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer).getByName(
            "main"
        ).compileClasspath
        args = getBaseArgs()
        workingDir = File(project.projectDir, "temp/testServer")
    }

    private fun getBaseArgs() = mutableListOf<String>().apply {
        add("--version=release")
        add("--versionType=release")
        add("--sourceDir=${File(project.projectDir, "build/classes/java/main").absolutePath}")
        add("--includeDir=${File(project.projectDir, "build/resources/main").absolutePath}")
        additionalSourceDirs.get().forEach { add("--sourceDir=${it.absolutePath}") }
        additionalIncludeDirs.get().forEach { add("--includeDir=${it.absolutePath}") }
    }

    @Suppress("RedundantOverride")
    override fun getArgs(): MutableList<String>? {
        return super.getArgs() // => (Mutable)List<String>!
    }

    fun addArgs(vararg args: String) {
        val current = this.args ?: mutableListOf()
        this.args = current.apply {
            addAll(args)
        }
    }

    @Inject
    override fun getObjectFactory(): ObjectFactory {
        throw UnsupportedOperationException()
    }
}
