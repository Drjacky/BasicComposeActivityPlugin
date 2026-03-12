package app.web.drjacky.basiccomposeactivityplugin

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Completes project generation after the IDE opens the new project window.
 *
 * Android Studio's internal module rendering pipeline
 * (`generateAndroidModule` → `CommonPluginsInserter`) can crash with
 * `IllegalStateException: Build model for root project not found` before
 * our template recipe executes.  Even when it doesn't crash, the pipeline
 * writes partial files through VFS that conflict with direct disk writes.
 *
 * This activity is the single point of file generation: it waits for the
 * rendering pipeline to finish (or fail), writes every project file to disk,
 * forces a VFS refresh so the IDE sees the new content, and triggers a
 * Gradle sync.
 */
class BasicComposeProjectSetupActivity : ProjectActivity {

    private val logger = Logger.getInstance(BasicComposeProjectSetupActivity::class.java)

    override suspend fun execute(project: Project) {
        val packageName = PendingTemplateConfig.consumeIfRecent() ?: return

        delay(5_000)

        val projectDir = File(project.basePath ?: return)

        if (File(projectDir, "build-logic/settings.gradle.kts").exists()) return

        val projectName = projectDir.name

        logger.info("Generating Basic Compose Activity project: $projectName ($packageName)")

        File(projectDir, "app").takeIf { it.exists() }?.deleteRecursively()

        ProjectFileGenerator.generateAll(projectDir, projectName, packageName)

        withContext(Dispatchers.EDT) {
            val localFs = LocalFileSystem.getInstance()
            val vDir = localFs.refreshAndFindFileByPath(projectDir.absolutePath)
            if (vDir != null) {
                VfsUtil.markDirtyAndRefresh(false, true, true, vDir)
                logger.info("VFS refreshed for $projectName")
            } else {
                logger.warn("Could not find VirtualFile for $projectDir")
            }
        }

        delay(2_000)

        withContext(Dispatchers.EDT) {
            triggerGradleSync()
        }

        logger.info("Basic Compose Activity project setup completed: $projectName")
    }

    private fun triggerGradleSync() {
        try {
            val actionManager = ActionManager.getInstance()
            val syncAction = actionManager.getAction("Android.SyncProject")
                ?: actionManager.getAction("ExternalSystem.RefreshAllProjects")
            if (syncAction != null) {
                actionManager.tryToExecute(syncAction, null, null, null, true)
                logger.info("Gradle sync triggered via action")
            } else {
                logger.warn("No sync action found — user should manually trigger Gradle sync")
            }
        } catch (e: Exception) {
            logger.warn("Could not trigger Gradle sync", e)
        }
    }
}
