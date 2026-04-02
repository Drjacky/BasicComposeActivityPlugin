import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.12.0"
}

group = "com.github.drjacky.basiccomposeactivityplugin"
version = "1.0.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        androidStudio("2025.3.2.6")

        bundledPlugin("org.jetbrains.android")
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("com.intellij.java")

        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            //untilBuild = "253.*"
        }

        changeNotes = """
            <h3>Fixed</h3>
            <ul>
                <li>Prevent plugin from triggering on non-Android projects (e.g., IntelliJ "Empty Project")</li>
            </ul>
            <h3>Added</h3>
            <ul>
                <li>GitHub Actions build workflow with plugin verification and draft release</li>
                <li>IntelliJ IDEA run configuration for testing</li>
            </ul>
        """.trimIndent()
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

intellijPlatformTesting {
    runIde {
        register("runIntelliJ") {
            type = IntelliJPlatformType.IntellijIdeaCommunity
            version = "2025.1"
        }
    }
}

kotlin {
    jvmToolchain(21)
}
