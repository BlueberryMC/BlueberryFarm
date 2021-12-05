package net.blueberrymc.blueberryFarm.tasks

import net.blueberrymc.blueberryFarm.getBlueberryConfig
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import java.io.File
import java.util.UUID
import javax.inject.Inject

open class RunClient: JavaExec() {
    @Internal val assetIndex: Property<String>
    @Internal val username: Property<String>
    @Internal val userType: Property<String>
    @Internal val uuid: Property<UUID>
    @Internal val accessToken: Property<String>
    @Internal val additionalSourceDirs: ListProperty<File>
    @Internal val additionalIncludeDirs: ListProperty<File>

    init {
        val factory = objectFactory
        mainClass.set("net.blueberrymc.client.main.ClientMain")
        val mcVersion = project.getBlueberryConfig().minecraftVersion.get().split(".")
        assetIndex = factory.property(String::class.java).value(if (mcVersion.size == 1) mcVersion[0] else "${mcVersion[0]}.${mcVersion[1]}")
        username = factory.property(String::class.java).value("Player${(Math.random() * 999).toInt()}")
        userType = factory.property(String::class.java).value("mojang")
        uuid = factory.property(UUID::class.java).value(UUID(0, 0))
        accessToken = factory.property(String::class.java).value("test")
        additionalSourceDirs = factory.listProperty(File::class.java).value(mutableListOf())
        additionalIncludeDirs = factory.listProperty(File::class.java).value(mutableListOf())
        classpath = ((project as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer).getByName(
            "main"
        ).compileClasspath
        args = getBaseArgs()
        workingDir = File(project.projectDir, "temp/testClient")
    }

    private fun getBaseArgs() = mutableListOf<String>().apply {
        add("--version=release")
        add("--assetIndex=${assetIndex.get()}")
        add("--versionType=release")
        add("--username=${username.get()}")
        add("--userType=${userType.get()}")
        add("--uuid=${uuid.get()}")
        add("--accessToken=${accessToken.get()}")
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
