package app.web.drjacky.basiccomposeactivityplugin.templates

fun buildLogicSettingsGradleKts(): String = """
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
""".trimIndent()

fun buildLogicConventionBuildGradleKts(): String = """
plugins {
    `kotlin-dsl`
}

group = "buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.composeGradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "app.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "app.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "app.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "app.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLint") {
            id = "app.android.lint"
            implementationClass = "AndroidLintConventionPlugin"
        }
        register("hilt") {
            id = "app.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "app.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
        register("androidDetekt") {
            id = "app.android.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }
    }
}
""".trimIndent()

fun androidApplicationConventionPlugin(): String = """
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "app.android.lint")
            apply(plugin = "app.android.detekt")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                testOptions {
                    animationsDisabled = true
                }
            }
        }
    }
}
""".trimIndent()

fun androidApplicationComposeConventionPlugin(): String = """
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
""".trimIndent()

fun androidLibraryConventionPlugin(): String {
    val tq = "\"\"\""
    return """
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "app.android.lint")
            apply(plugin = "app.android.detekt")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    testOptions.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    lint.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                }

                testOptions {
                    animationsDisabled = true
                }

                resourcePrefix = path.split(${tq}\W${tq}.toRegex())
                    .drop(1)
                    .distinct()
                    .joinToString(separator = "_")
                    .lowercase() + "_"
            }

            dependencies {
                add("androidTestImplementation", libs.findLibrary("kotlin.test").get())
                add("testImplementation", libs.findLibrary("kotlin.test").get())
                add("testImplementation", libs.findLibrary("junit").get())
            }
        }
    }
}
""".trimIndent()
}

fun androidLibraryComposeConventionPlugin(): String = """
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}
""".trimIndent()

fun androidLintConventionPlugin(): String = """
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configureLint) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configureLint) }

                else -> {
                    pluginManager.apply("com.android.lint")
                    configure<Lint> { configureLint() }
                }
            }
        }
    }
}

private fun Lint.configureLint() {
    xmlReport = true
    sarifReport = true
    checkDependencies = true
    disable += "GradleDependency"
}
""".trimIndent()

fun androidDetektConventionPlugin(): String = """
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val detektPluginId = libs.findPlugin("detekt").get().get().pluginId

        pluginManager.apply(detektPluginId)

        dependencies {
            add("detektPlugins", libs.findLibrary("compose.rules.detekt").get())
            add("detektPlugins", libs.findLibrary("detekt.formatting").get())
        }

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig.set(true)
            parallel.set(true)

            basePath.set(rootProject.layout.projectDirectory)
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))

            val moduleConfig = project.file("detekt.yml")
            if (moduleConfig.exists()) {
                config.from(moduleConfig)
            }

            baseline.set(project.file("detekt-baseline.xml"))
        }

        tasks.withType<Detekt>().configureEach {
            jvmTarget.set("17")

            reports {
                checkstyle.required.set(true)
                html.required.set(true)
                sarif.required.set(true)
                markdown.required.set(false)
            }

            if (project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
                val javaExtension = extensions.findByType(JavaPluginExtension::class.java)
                javaExtension?.let {
                    classpath.from(it.sourceSets.getByName("main").compileClasspath)
                }
            }
        }

        tasks.withType<DetektCreateBaselineTask>().configureEach {
            jvmTarget.set("17")
        }
    }
}
""".trimIndent()

fun hiltConventionPlugin(): String = """
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            apply(plugin = "com.google.dagger.hilt.android")

            val hiltCompiler = libs.findLibrary("hilt.compiler").get()
            val hiltAndroidTesting = libs.findLibrary("hilt.android.testing").get()

            dependencies {
                add("implementation", libs.findLibrary("hilt.android").get())
                add("ksp", hiltCompiler)

                add("kspTest", hiltCompiler)
                add("testImplementation", hiltAndroidTesting)
                add("kspAndroidTest", hiltCompiler)
                add("androidTestImplementation", hiltAndroidTesting)
            }
        }
    }
}
""".trimIndent()

fun kotlinSerializationConventionPlugin(): String = """
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("kotlinx.serialization").get())
            }
        }
    }
}
""".trimIndent()

fun androidComposeConfig(): String = """
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.buildFeatures.compose = true

    dependencies {
        val bom = libs.findLibrary("androidx.compose.bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))
        add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        val enableMetrics = project.providers.gradleProperty("enableComposeCompilerMetrics")
        if (enableMetrics.orNull.toBoolean()) {
            metricsDestination.set(project.layout.buildDirectory.dir("compose-metrics"))
        }

        val enableReports = project.providers.gradleProperty("enableComposeCompilerReports")
        if (enableReports.orNull.toBoolean()) {
            reportsDestination.set(project.layout.buildDirectory.dir("compose-reports"))
        }

        stabilityConfigurationFile.set(
            rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
        )
    }
}
""".trimIndent()

fun kotlinAndroidConfig(): String = """
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig.apply {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)

            freeCompilerArgs.addAll(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("androidx.core.desugaring").get())
    }
}
""".trimIndent()

fun projectExtensionsConfig(): String = """
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
""".trimIndent()
