package app.web.drjacky.basiccomposeactivityplugin

import app.web.drjacky.basiccomposeactivityplugin.templates.appBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.appColorsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appManifestXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appNavigation
import app.web.drjacky.basiccomposeactivityplugin.templates.appStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appStylesXml
import app.web.drjacky.basiccomposeactivityplugin.templates.appStylesNightXml
import app.web.drjacky.basiccomposeactivityplugin.templates.addSampleScreen
import app.web.drjacky.basiccomposeactivityplugin.templates.androidApplicationComposeConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidApplicationConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidComposeConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.androidDetektConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLibraryComposeConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLibraryConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.androidLintConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.buildLogicConventionBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.buildLogicSettingsGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.composeCompilerConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.coreCommonBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreDomainBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreDomainSampleItem
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiColorsNightXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiShapeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiThemeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.coreUiTypeKt
import app.web.drjacky.basiccomposeactivityplugin.templates.detektBaselineXml
import app.web.drjacky.basiccomposeactivityplugin.templates.detektYml
import app.web.drjacky.basiccomposeactivityplugin.templates.featureSampleBuildGradleKts
import app.web.drjacky.basiccomposeactivityplugin.templates.featureSampleStringsXml
import app.web.drjacky.basiccomposeactivityplugin.templates.gradleProperties
import app.web.drjacky.basiccomposeactivityplugin.templates.hiltConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.kotlinAndroidConfig
import app.web.drjacky.basiccomposeactivityplugin.templates.kotlinSerializationConventionPlugin
import app.web.drjacky.basiccomposeactivityplugin.templates.libsVersionsToml
import app.web.drjacky.basiccomposeactivityplugin.templates.mainActivity
import app.web.drjacky.basiccomposeactivityplugin.templates.myApp
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
import com.android.tools.idea.npw.module.recipes.generateManifest
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import java.io.File

fun RecipeExecutor.basicComposeActivityRecipe(
    moduleData: ModuleTemplateData,
    packageName: String,
) {
    val (_, srcOut, resOut, manifestOut) = moduleData
    val projectRoot = moduleData.rootDir.parentFile
    val projectName = projectRoot.name
    val pkgPath = packageName.replace(".", "/")

    generateManifest(hasApplicationBlock = true)

    mergeXml(
        appManifestXml(packageName),
        manifestOut.resolve("AndroidManifest.xml"),
    )

    generateRootProjectFiles(projectRoot, projectName, packageName)
    generateBuildLogic(projectRoot)
    generateAppModule(projectRoot, srcOut, resOut, packageName, projectName)
    generateCoreCommon(projectRoot, pkgPath, packageName)
    generateCoreDomain(projectRoot, pkgPath, packageName)
    generateCoreUi(projectRoot, pkgPath, packageName)
    generateFeatureSample(projectRoot, pkgPath, packageName)
    generateConfigFiles(projectRoot)
}

private fun RecipeExecutor.generateRootProjectFiles(
    projectRoot: File,
    projectName: String,
    packageName: String,
) {
    overwrite(projectRoot, "settings.gradle.kts", settingsGradleKts(projectName))
    overwrite(projectRoot, "build.gradle.kts", rootBuildGradleKts())
    overwrite(projectRoot, "gradle.properties", gradleProperties())
    overwrite(projectRoot, "gradle/libs.versions.toml", libsVersionsToml())
    save(composeCompilerConfig(packageName), projectRoot.resolve("compose_compiler_config.conf"))
}

private fun RecipeExecutor.generateBuildLogic(projectRoot: File) {
    val conventionSrc = "build-logic/convention/src/main/kotlin"
    val configSrc = "$conventionSrc/config"

    save(buildLogicSettingsGradleKts(), projectRoot.resolve("build-logic/settings.gradle.kts"))
    save(buildLogicConventionBuildGradleKts(), projectRoot.resolve("build-logic/convention/build.gradle.kts"))

    save(androidApplicationConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidApplicationConventionPlugin.kt"))
    save(androidApplicationComposeConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidApplicationComposeConventionPlugin.kt"))
    save(androidLibraryConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidLibraryConventionPlugin.kt"))
    save(androidLibraryComposeConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidLibraryComposeConventionPlugin.kt"))
    save(androidLintConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidLintConventionPlugin.kt"))
    save(androidDetektConventionPlugin(), projectRoot.resolve("$conventionSrc/AndroidDetektConventionPlugin.kt"))
    save(hiltConventionPlugin(), projectRoot.resolve("$conventionSrc/HiltConventionPlugin.kt"))
    save(kotlinSerializationConventionPlugin(), projectRoot.resolve("$conventionSrc/KotlinSerializationConventionPlugin.kt"))

    save(androidComposeConfig(), projectRoot.resolve("$configSrc/AndroidCompose.kt"))
    save(kotlinAndroidConfig(), projectRoot.resolve("$configSrc/KotlinAndroid.kt"))
    save(projectExtensionsConfig(), projectRoot.resolve("$configSrc/ProjectExtensions.kt"))
}

private fun RecipeExecutor.generateAppModule(
    projectRoot: File,
    srcOut: File,
    resOut: File,
    packageName: String,
    projectName: String,
) {
    overwrite(projectRoot, "app/build.gradle.kts", appBuildGradleKts(packageName))

    save(mainActivity(packageName), srcOut.resolve("MainActivity.kt"))
    save(myApp(packageName), srcOut.resolve("MyApp.kt"))
    save(appNavigation(packageName), srcOut.resolve("navigation/AppNavigation.kt"))
    save(sampleApplication(packageName), srcOut.resolve("application/SampleApplication.kt"))

    save(appStringsXml(projectName), resOut.resolve("values/strings.xml"))
    save(appColorsXml(), resOut.resolve("values/colors.xml"))
    save(appStylesXml(), resOut.resolve("values/styles.xml"))
    save(appStylesNightXml(), resOut.resolve("values-night/styles.xml"))
}

private fun RecipeExecutor.generateCoreCommon(
    projectRoot: File,
    pkgPath: String,
    packageName: String,
) {
    save(
        coreCommonBuildGradleKts(packageName),
        projectRoot.resolve("core/common/build.gradle.kts"),
    )
}

private fun RecipeExecutor.generateCoreDomain(
    projectRoot: File,
    pkgPath: String,
    packageName: String,
) {
    val domainSrc = "core/domain/src/main/kotlin/$pkgPath/core/domain"

    save(
        coreDomainBuildGradleKts(packageName),
        projectRoot.resolve("core/domain/build.gradle.kts"),
    )
    save(
        coreDomainSampleItem(packageName),
        projectRoot.resolve("$domainSrc/entity/SampleItem.kt"),
    )
}

private fun RecipeExecutor.generateCoreUi(
    projectRoot: File,
    pkgPath: String,
    packageName: String,
) {
    val uiSrc = "core/ui/src/main/kotlin/$pkgPath/core/ui"
    val uiRes = "core/ui/src/main/res"

    save(coreUiBuildGradleKts(packageName), projectRoot.resolve("core/ui/build.gradle.kts"))

    save(coreUiColorKt(packageName), projectRoot.resolve("$uiSrc/theme/Color.kt"))
    save(coreUiShapeKt(packageName), projectRoot.resolve("$uiSrc/theme/Shape.kt"))
    save(coreUiTypeKt(packageName), projectRoot.resolve("$uiSrc/theme/Type.kt"))
    save(coreUiThemeKt(packageName), projectRoot.resolve("$uiSrc/theme/Theme.kt"))

    save(coreUiStringsXml(), projectRoot.resolve("$uiRes/values/strings.xml"))
    save(coreUiColorsXml(), projectRoot.resolve("$uiRes/values/colors.xml"))
    save(coreUiColorsNightXml(), projectRoot.resolve("$uiRes/values-night/colors.xml"))
}

private fun RecipeExecutor.generateFeatureSample(
    projectRoot: File,
    pkgPath: String,
    packageName: String,
) {
    val sampleSrc = "feature/sample/src/main/kotlin/$pkgPath/feature/sample"
    val sampleRes = "feature/sample/src/main/res"

    save(
        featureSampleBuildGradleKts(packageName),
        projectRoot.resolve("feature/sample/build.gradle.kts"),
    )

    save(sampleDestination(packageName), projectRoot.resolve("$sampleSrc/navigation/SampleDestination.kt"))
    save(sampleNavigator(packageName), projectRoot.resolve("$sampleSrc/navigation/SampleNavigator.kt"))
    save(sampleGraph(packageName), projectRoot.resolve("$sampleSrc/navigation/SampleGraph.kt"))

    save(sampleListScreen(packageName), projectRoot.resolve("$sampleSrc/list/SampleListScreen.kt"))
    save(sampleItemRow(packageName), projectRoot.resolve("$sampleSrc/list/SampleItemRow.kt"))
    save(addSampleScreen(packageName), projectRoot.resolve("$sampleSrc/add/AddSampleScreen.kt"))

    save(sampleViewModel(packageName), projectRoot.resolve("$sampleSrc/SampleViewModel.kt"))

    save(featureSampleStringsXml(), projectRoot.resolve("$sampleRes/values/strings.xml"))
}

private fun RecipeExecutor.generateConfigFiles(projectRoot: File) {
    save(detektYml(), projectRoot.resolve("config/detekt/detekt.yml"))
    save(detektBaselineXml(), projectRoot.resolve("config/detekt/baseline.xml"))
}

private fun overwrite(projectRoot: File, path: String, content: String) {
    val file = File(projectRoot, path)
    file.parentFile.mkdirs()
    file.writeText(content)
}
