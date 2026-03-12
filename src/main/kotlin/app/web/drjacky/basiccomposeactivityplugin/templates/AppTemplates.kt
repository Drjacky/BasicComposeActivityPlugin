package app.web.drjacky.basiccomposeactivityplugin.templates

fun appBuildGradleKts(packageName: String): String = """
plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.android.application.compose)
    alias(libs.plugins.app.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.app.kotlin.serialization)
}

android {
    namespace = "$packageName"

    defaultConfig {
        applicationId = "$packageName"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/ui-tooling_release.kotlin_module",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            )
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.feature.sample)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.java.inject)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.activity.compose)

    implementation(libs.coil.compose)
    implementation(libs.google.material)
    debugImplementation(libs.leakcanary)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.espresso.core)
}
""".trimIndent()

fun appManifestXml(packageName: String): String = """
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:name=".application.SampleApplication"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
""".trimIndent()

fun mainActivity(packageName: String): String = """
package $packageName

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MyApp()
        }
    }
}
""".trimIndent()

fun myApp(packageName: String): String = """
package $packageName

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import $packageName.core.ui.theme.BaseTheme
import $packageName.navigation.AppNavigation

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
) {
    BaseTheme {
        AppNavigation(modifier = modifier)
    }
}

@Preview
@Composable
private fun MyAppPreview() {
    MyApp()
}
""".trimIndent()

fun appNavigation(packageName: String): String = """
package $packageName.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import $packageName.feature.sample.SampleViewModel
import $packageName.feature.sample.navigation.SampleDestination
import $packageName.feature.sample.navigation.SampleNavigator
import $packageName.feature.sample.navigation.sampleGraph

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(SampleDestination.SampleList as NavKey)
    val sampleViewModel: SampleViewModel = hiltViewModel()

    val navigator = remember(backStack) {
        object : SampleNavigator {
            override fun navigateToAddSample() {
                backStack.add(SampleDestination.AddSample)
            }

            override fun navigateBack() {
                backStack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            sampleGraph(
                navigator = navigator,
                sharedViewModel = { sampleViewModel },
            )
        },
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
    )
}
""".trimIndent()

fun sampleApplication(packageName: String): String = """
package $packageName.application

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import $packageName.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SampleApplication : Application() {

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }
        super.onCreate()
    }
}
""".trimIndent()

fun appStringsXml(projectName: String): String = """
<resources>
    <string name="app_name">$projectName</string>
</resources>
""".trimIndent()

fun appColorsXml(): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="colorPrimary">#6200EE</color>
    <color name="colorPrimaryDark">#3700B3</color>
    <color name="colorAccent">#FF009688</color>
    <color name="ic_launcher_background">#FFFFFF</color>
</resources>
""".trimIndent()

fun appStylesXml(): String = """
<resources>

    <style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>
""".trimIndent()

fun appStylesNightXml(): String = """
<resources>

    <style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

</resources>
""".trimIndent()

fun proguardRules(): String = """
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
""".trimIndent()

fun icLauncherForegroundXml(): String = """
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
  <path
      android:fillColor="#3DDC84"
      android:pathData="M0,0h108v108h-108z"/>
  <path
      android:fillColor="#FFFFFF"
      android:pathData="M36.6,72.9L36.6,36L44.4,36L44.4,66L60,66L60,72.9Z"/>
</vector>
""".trimIndent()

fun adaptiveIconXml(): String = """
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
""".trimIndent()

fun adaptiveIconRoundXml(): String = """
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
""".trimIndent()
