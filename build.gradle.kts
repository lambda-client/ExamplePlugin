import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.spongepowered.asm.gradle.plugins.MixinExtension

val pluginGroup: String by project
val pluginVersion: String by project
val apiVersion: String by project

group = pluginGroup
version = pluginVersion

buildscript {
    repositories {
        mavenCentral()
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }

    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:4.+")
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    idea
    java
    kotlin("jvm")
}

apply {
    plugin("net.minecraftforge.gradle")
    plugin("org.spongepowered.mixin")
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://impactdevelopment.github.io/maven/")
    maven("https://maven.pkg.github.com/lambda-client/lambda-api") {
        val githubProperty = runCatching {
            loadProperties("${projectDir.absolutePath}/github.properties")
        }.getOrNull()

        credentials {
            username = githubProperty?.getProperty("username") ?: System.getenv("GITHUB_ACTOR")
            password = githubProperty?.getProperty("token") ?: System.getenv("GITHUB_ACTOR")
        }
    }
}

val library: Configuration by configurations.creating

val minecraftVersion: String by project
val forgeVersion: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project

val kotlinxCoroutinesVersion: String by project

dependencies {
    // Jar packaging
    fun ModuleDependency.exclude(moduleName: String): ModuleDependency {
        return exclude(mapOf("module" to moduleName))
    }

    fun jarOnly(dependencyNotation: Any) {
        library(dependencyNotation)
    }

    fun jarAndImplementation(dependencyNotation: Any) {
        library(dependencyNotation)
    }

    // Forge
    "minecraft"("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    // Dependencies
    implementation("com.lambda:lambda-api:$apiVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    implementation("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        exclude("commons-io")
        exclude("gson")
        exclude("guava")
        exclude("launchwrapper")
        exclude("log4j-core")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.2:processor") {
        exclude("gson")
    }

    implementation("com.github.cabaletta:baritone:1.2.14")
}

configure<MixinExtension> {
    add(sourceSets.main.get(), "mixins.example.json")
}

configure<UserDevExtension> {
    mappings(
        mapOf(
            "channel" to mappingsChannel,
            "version" to mappingsVersion
        )
    )

    runs {
        create("client") {
            workingDirectory = project.file("run").path
            ideaModule("${project.name}.main")

            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP",
                    "forge.logging.console.level" to "info",
                    "fml.coreMods.load" to "com.lambda.client.LambdaCoreMod",
                    "mixin.env.disableRefMap" to "true"
                )
            )
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                "-Xlambdas=indy"
            )
        }
    }

    processResources {
        filesMatching("plugin_info.json") {
            expand("version" to project.version)
        }
    }

    jar {
        from(
            library.map {
                if (it.isDirectory) it else zipTree(it)
            }
        )
    }

    register<Task>("genRuns") {
        group = "ide"
        doLast {
            file(File(rootDir, ".idea/runConfigurations/${project.name}_runClient.xml")).writer().use {
                it.write(
                    """
                        <component name="ProjectRunConfigurationManager">
                          <configuration default="false" name="${project.name} runClient" type="Application" factoryName="Application">
                            <envs>
                              <env name="MCP_TO_SRG" value="${"$"}PROJECT_DIR$/build/createSrgToMcp/output.srg" />
                              <env name="MOD_CLASSES" value="${"$"}PROJECT_DIR$/build/resources/main;${"$"}PROJECT_DIR$/build/classes/java/main;${"$"}PROJECT_DIR$/build/classes/kotlin/main" />
                              <env name="mainClass" value="net.minecraft.launchwrapper.Launch" />
                              <env name="MCP_MAPPINGS" value="${mappingsChannel}_$mappingsVersion" />
                              <env name="FORGE_VERSION" value="$forgeVersion" />
                              <env name="assetIndex" value="${minecraftVersion.substringBeforeLast(".")}" />
                              <env name="assetDirectory" value="${gradle.gradleUserHomeDir.path.replace("\\", "/")}/caches/forge_gradle/assets" />
                              <env name="nativesDirectory" value="${"$"}PROJECT_DIR$/build/natives" />
                              <env name="FORGE_GROUP" value="net.minecraftforge" />
                              <env name="tweakClass" value="net.minecraftforge.fml.common.launcher.FMLTweaker" />
                              <env name="MC_VERSION" value="${"$"}{MC_VERSION}" />
                              <env name="DEV_PLUGIN" value="true" />
                            </envs>
                            <option name="MAIN_CLASS_NAME" value="net.minecraftforge.legacydev.MainClient" />
                            <module name="${project.name}.main" />
                            <option name="PROGRAM_PARAMETERS" value="--width 1280 --height 720" />
                            <option name="VM_PARAMETERS" value="-Dforge.logging.console.level=info -Dforge.logging.markers=SCAN,REGISTRIES,REGISTRYDUMP -Dmixin.env.disableRefMap=true -Dfml.coreMods.load=com.lambda.client.LambdaCoreMod" />
                            <option name="WORKING_DIRECTORY" value="${"$"}PROJECT_DIR$/run" />
                            <method v="2">
                              <option name="Gradle.BeforeRunTask" enabled="true" tasks="prepareRunClient" externalProjectPath="${"$"}PROJECT_DIR$" />
                            </method>
                          </configuration>
                        </component>
                    """.trimIndent()
                )
            }
        }
    }
}