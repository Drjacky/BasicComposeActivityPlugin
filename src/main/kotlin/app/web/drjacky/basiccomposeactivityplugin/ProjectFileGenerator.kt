package app.web.drjacky.basiccomposeactivityplugin

import app.web.drjacky.basiccomposeactivityplugin.templates.adaptiveIconRoundXml
import app.web.drjacky.basiccomposeactivityplugin.templates.adaptiveIconXml
import app.web.drjacky.basiccomposeactivityplugin.templates.addSampleScreen
import app.web.drjacky.basiccomposeactivityplugin.templates.androidApplicationComposeConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidApplicationConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidComposeConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.androidDetektConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLibraryComposeConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLibraryConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLintConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.appBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.appColorsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appManifestXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appNavigation
import app.web.drjacky.basiccomposeactivityplugin.templates.appStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appStylesNightXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appStylesXml
import app.web.drjacky.basiccomposeactivityplugin.templates.buildLogicConventionBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.buildLogicSettingsGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.composeCompilerConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.coreCommonBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreDomainBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreDomainSampleItem
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorsNightXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiShapeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiThemeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiTypeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.detektBaselineXml
import app.web.drjacky.basiccomposeactivityplugin.templates.detektYml
import app.web.drjacky.basiccomposeactivityplugin.templates.featureSampleBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.featureSampleStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.gradleProperties
import app.web.drjacky.basiccomposeactivityplugin.templates.gradleWrapperProperties
import app.web.drjacky.basiccomposeactivityplugin.templates.hiltConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.icLauncherForegroundXml
import app.web.drjacky.basiccomposeactivityplugin.templates.kotlinAndroidConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.kotlinSerializationConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.libsVersionsToml
import app.web.drjacky.basiccomposeactivityplugin.templates.mainActivity
import app.web.drjacky.basiccomposeactivityplugin.templates.myApp
import app.web.drjacky.basiccomposeactivityplugin.templates.proguardRules
import app.web.drjacky.basiccomposeactivityplugin.templates.projectExtensionsConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.rootBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleApplication
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleDestination
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleGraph
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleItemRow
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleListScreen
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleNavigator
import app.web.drjacky.basiccomposeactivityplugin.templates.sampleViewModel
import app.web.drjacky.basiccomposeactivityplugin.templates.settingsGradleKts
import java.io.File

object ProjectFileGenerator {

    fun generateAll(
        projectRoot: File,
        projectName: String,
        packageName: String,
    ) {
        val pkgPath = packageName.replace(".", "/")

        generateRootProjectFiles(projectRoot, projectName, packageName)
        generateBuildLogic(projectRoot)
        generateAppModule(projectRoot, packageName, projectName, pkgPath)
        generateCoreCommon(projectRoot, packageName)
        generateCoreDomain(projectRoot, pkgPath, packageName)
        generateCoreUi(projectRoot, pkgPath, packageName)
        generateFeatureSample(projectRoot, pkgPath, packageName)
        generateConfigFiles(projectRoot)
    }

    private fun generateRootProjectFiles(
        projectRoot: File,
        projectName: String,
        packageName: String,
    ) {
        writeFile(projectRoot, "settings.gradle.kts", settingsGradleKts(projectName))
        writeFile(projectRoot, "build.gradle.kts", rootBuildGradleKts())
        writeFile(projectRoot, "gradle.properties", gradleProperties())
        writeFile(projectRoot, "gradle/libs.versions.toml", libsVersionsToml())
        writeFile(projectRoot, "gradle/wrapper/gradle-wrapper.properties", gradleWrapperProperties())
        writeFile(projectRoot, "compose_compiler_config.conf", composeCompilerConfig(packageName))
    }

    private fun generateBuildLogic(projectRoot: File) {
        val conventionSrc = "build-logic/convention/src/main/kotlin"
        val configSrc = "$conventionSrc/config"

        writeFile(projectRoot, "build-logic/settings.gradle.kts", buildLogicSettingsGradleKts())
        writeFile(projectRoot, "build-logic/convention/build.gradle.kts", buildLogicConventionBuildGradleKts())

        writeFile(projectRoot, "$conventionSrc/AndroidApplicationConventionPlugin.kt", androidApplicationConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/AndroidApplicationComposeConventionPlugin.kt", androidApplicationComposeConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/AndroidLibraryConventionPlugin.kt", androidLibraryConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/AndroidLibraryComposeConventionPlugin.kt", androidLibraryComposeConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/AndroidLintConventionPlugin.kt", androidLintConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/AndroidDetektConventionPlugin.kt", androidDetektConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/HiltConventionPlugin.kt", hiltConventionPlugin())
        writeFile(projectRoot, "$conventionSrc/KotlinSerializationConventionPlugin.kt", kotlinSerializationConventionPlugin())

        writeFile(projectRoot, "$configSrc/AndroidCompose.kt", androidComposeConfig())
        writeFile(projectRoot, "$configSrc/KotlinAndroid.kt", kotlinAndroidConfig())
        writeFile(projectRoot, "$configSrc/ProjectExtensions.kt", projectExtensionsConfig())
    }

    private fun generateAppModule(
        projectRoot: File,
        packageName: String,
        projectName: String,
        pkgPath: String,
    ) {
        val appSrc = "app/src/main/kotlin/$pkgPath"
        val appRes = "app/src/main/res"

        writeFile(projectRoot, "app/build.gradle.kts", appBuildGradleKts(packageName))
        writeFile(projectRoot, "app/proguard-rules.pro", proguardRules())
        writeFile(projectRoot, "app/src/main/AndroidManifest.xml", appManifestXml(packageName))

        writeFile(projectRoot, "$appSrc/MainActivity.kt", mainActivity(packageName))
        writeFile(projectRoot, "$appSrc/MyApp.kt", myApp(packageName))
        writeFile(projectRoot, "$appSrc/navigation/AppNavigation.kt", appNavigation(packageName))
        writeFile(projectRoot, "$appSrc/application/SampleApplication.kt", sampleApplication(packageName))

        writeFile(projectRoot, "$appRes/values/strings.xml", appStringsXml(projectName))
        writeFile(projectRoot, "$appRes/values/colors.xml", appColorsXml())
        writeFile(projectRoot, "$appRes/values/styles.xml", appStylesXml())
        writeFile(projectRoot, "$appRes/values-night/styles.xml", appStylesNightXml())

        writeFile(projectRoot, "$appRes/drawable/ic_launcher_foreground.xml", icLauncherForegroundXml())
        writeFile(projectRoot, "$appRes/mipmap-anydpi-v26/ic_launcher.xml", adaptiveIconXml())
        writeFile(projectRoot, "$appRes/mipmap-anydpi-v26/ic_launcher_round.xml", adaptiveIconRoundXml())
    }

    private fun generateCoreCommon(projectRoot: File, packageName: String) {
        writeFile(projectRoot, "core/common/build.gradle.kts", coreCommonBuildGradleKts(packageName))
    }

    private fun generateCoreDomain(projectRoot: File, pkgPath: String, packageName: String) {
        val domainSrc = "core/domain/src/main/kotlin/$pkgPath/core/domain"
        writeFile(projectRoot, "core/domain/build.gradle.kts", coreDomainBuildGradleKts(packageName))
        writeFile(projectRoot, "$domainSrc/entity/SampleItem.kt", coreDomainSampleItem(packageName))
    }

    private fun generateCoreUi(projectRoot: File, pkgPath: String, packageName: String) {
        val uiSrc = "core/ui/src/main/kotlin/$pkgPath/core/ui"
        val uiRes = "core/ui/src/main/res"

        writeFile(projectRoot, "core/ui/build.gradle.kts", coreUiBuildGradleKts(packageName))

        writeFile(projectRoot, "$uiSrc/theme/Color.kt", coreUiColorKt(packageName))
        writeFile(projectRoot, "$uiSrc/theme/Shape.kt", coreUiShapeKt(packageName))
        writeFile(projectRoot, "$uiSrc/theme/Type.kt", coreUiTypeKt(packageName))
        writeFile(projectRoot, "$uiSrc/theme/Theme.kt", coreUiThemeKt(packageName))

        writeFile(projectRoot, "$uiRes/values/strings.xml", coreUiStringsXml())
        writeFile(projectRoot, "$uiRes/values/colors.xml", coreUiColorsXml())
        writeFile(projectRoot, "$uiRes/values-night/colors.xml", coreUiColorsNightXml())
    }

    private fun generateFeatureSample(projectRoot: File, pkgPath: String, packageName: String) {
        val sampleSrc = "feature/sample/src/main/kotlin/$pkgPath/feature/sample"
        val sampleRes = "feature/sample/src/main/res"

        writeFile(projectRoot, "feature/sample/build.gradle.kts", featureSampleBuildGradleKts(packageName))

        writeFile(projectRoot, "$sampleSrc/navigation/SampleDestination.kt", sampleDestination(packageName))
        writeFile(projectRoot, "$sampleSrc/navigation/SampleNavigator.kt", sampleNavigator(packageName))
        writeFile(projectRoot, "$sampleSrc/navigation/SampleGraph.kt", sampleGraph(packageName))

        writeFile(projectRoot, "$sampleSrc/list/SampleListScreen.kt", sampleListScreen(packageName))
        writeFile(projectRoot, "$sampleSrc/list/SampleItemRow.kt", sampleItemRow(packageName))
        writeFile(projectRoot, "$sampleSrc/add/AddSampleScreen.kt", addSampleScreen(packageName))

        writeFile(projectRoot, "$sampleSrc/SampleViewModel.kt", sampleViewModel(packageName))

        writeFile(projectRoot, "$sampleRes/values/strings.xml", featureSampleStringsXml())
    }

    private fun generateConfigFiles(projectRoot: File) {
        writeFile(projectRoot, "config/detekt/detekt.yml", detektYml())
        writeFile(projectRoot, "config/detekt/baseline.xml", detektBaselineXml())
    }

    private fun writeFile(root: File, path: String, content: String) {
        val file = File(root, path)
        file.parentFile.mkdirs()
        file.writeText(content)
    }
}
