package star.intellijplugin.pkgfinder.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.HttpRequests
import com.intellij.util.ui.UIUtil
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.maven.Dependency
import star.intellijplugin.pkgfinder.util.Icons
import star.intellijplugin.pkgfinder.util.showInformationNotification
import java.io.File
import javax.swing.Icon

/**
 * 下载 Maven 构件（源码、JAR 包、Javadoc 等）
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-07-13
 */
class DownloadArtifactAction(
    private val dependency: Dependency,
    private val downloadUrl: String,
    text: String,
    description: String,
    icon: Icon,
    private val ec: String
) : AnAction(text, description, icon) {

    override fun actionPerformed(e: AnActionEvent) {
        downloadArtifact(dependency, downloadUrl, ec)
    }

    companion object {
        fun forSource(dependency: Dependency, url: String) =
            DownloadArtifactAction(
                dependency,
                url,
                "Download Source",
                "Download source",
                Icons.JAR_SOURCES.getThemeBasedIcon(),
                "-sources.jar"
            )

        fun forJar(dependency: Dependency, url: String) =
            DownloadArtifactAction(
                dependency,
                url,
                "Download JAR",
                "Download jar",
                Icons.JAR.getThemeBasedIcon(),
                ".jar"
            )

        fun forJavadoc(dependency: Dependency, url: String) =
            DownloadArtifactAction(
                dependency,
                url,
                "Download Javadoc",
                "Download javadoc",
                Icons.JAVADOC.getThemeBasedIcon(),
                "-javadoc.jar"
            )

        fun forPom(dependency: Dependency, url: String) =
            DownloadArtifactAction(
                dependency,
                url,
                "Download Pom",
                "Download pom",
                Icons.JAVADOC.getThemeBasedIcon(),
                ".pom"
            )

        fun forModule(dependency: Dependency, url: String) =
            DownloadArtifactAction(
                dependency,
                url,
                "Download Module",
                "Download module",
                Icons.JAVADOC.getThemeBasedIcon(),
                ".module"
            )
    }

    private fun downloadArtifact(dependency: Dependency, downloadUrl: String, ec: String) {
        val artifactId = dependency.artifactId
        val version = dependency.version
        val savePath = FileUtil.join(System.getProperty("user.home"), "Downloads", "$artifactId-$version$ec")

        object : Task.Backgroundable(null, message("download.task.title"), true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val file = File(savePath)
                    HttpRequests.request(downloadUrl)
                        .productNameAsUserAgent()
                        .saveToFile(file, indicator)
                } catch (e: Exception) {
                    Messages.showErrorDialog(
                        message(
                            "download.error.message",
                            e.localizedMessage ?: "Unknown error"
                        ), message("download.error.title")
                    )
                }
            }

            override fun onSuccess() {
                UIUtil.invokeLaterIfNeeded {
                    showInformationNotification(message("download.success.message", savePath))
                }
            }
        }.queue()
    }
}
