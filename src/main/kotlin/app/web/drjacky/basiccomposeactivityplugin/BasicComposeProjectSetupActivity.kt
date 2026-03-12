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
 */
class BasicComposeProjectSetupActivity : ProjectActivity {

    private val logger = Logger.getInstance(BasicComposeProjectSetupActivity::class.java)

    override suspend fun execute(project: Project) {
        val packageName = PendingTemplateConfig.consumeIfRecent() ?: return

        // Small delay to let the rendering pipeline finish (or fail).
        delay(2_000)

        val projectDir = File(project.basePath ?: return)

        if (File(projectDir, "app/build.gradle.kts").exists()) return
        if (File(projectDir, "build-logic").exists()) return

        val projectName = projectDir.name

        logger.info("Generating Basic Compose Activity project files: $projectName ($packageName)")

        ProjectFileGenerator.generateAll(projectDir, projectName, packageName)

        VfsUtil.markDirtyAndRefresh(false, true, true, projectDir)

        logger.info("Basic Compose Activity project setup completed: $projectName")
    }
}
