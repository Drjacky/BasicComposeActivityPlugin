package app.web.drjacky.basiccomposeactivityplugin

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VfsUtil
import kotlinx.coroutines.delay
import java.io.File

/**
 * Completes project generation when Android Studio's internal module rendering
 * pipeline fails (the `generateAndroidModule` → `CommonPluginsInserter` path
 * throws `IllegalStateException: Build model for root project not found`).
 *
 * The IDE's `ProjectTemplateRenderer` creates root Gradle files before the
 * module renderer runs.  When the module renderer fails, our template's recipe
 * never executes.  This activity detects that situation and writes every file
 * that the recipe would have created.
 *
 * Detection: `build-logic/settings.gradle.kts` is unique to our template and
 * is never created by the standard module rendering pipeline.  If it is absent
 * the recipe hasn't run and we need to generate everything.
 *
 * Note: `generateCommonModule` creates a partial `app/` directory (including
 * `app/build.gradle.kts`) before it crashes, so we cannot use that file for
 * detection.  We delete the partial `app/` directory before regenerating.
 */
class BasicComposeProjectSetupActivity : ProjectActivity {

    private val logger = Logger.getInstance(BasicComposeProjectSetupActivity::class.java)

    override suspend fun execute(project: Project) {
        val packageName = PendingTemplateConfig.consumeIfRecent() ?: return

        // Let the rendering pipeline finish (or fail) before we inspect the disk.
        delay(3_000)

        val projectDir = File(project.basePath ?: return)

        // build-logic is only created by our generator — if it exists, we're done.
        if (File(projectDir, "build-logic/settings.gradle.kts").exists()) return

        val projectName = projectDir.name

        logger.info("Generating Basic Compose Activity project: $projectName ($packageName)")

        // Remove the partial app/ directory left by the failed generateCommonModule
        // so our generator can write a clean version.
        File(projectDir, "app").takeIf { it.exists() }?.deleteRecursively()

        ProjectFileGenerator.generateAll(projectDir, projectName, packageName)

        VfsUtil.markDirtyAndRefresh(false, true, true, projectDir)

        logger.info("Basic Compose Activity project setup completed: $projectName")
    }
}
