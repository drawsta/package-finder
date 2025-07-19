package star.intellijplugin.pkgfinder.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import star.intellijplugin.pkgfinder.maven.CentralDependency
import star.intellijplugin.pkgfinder.maven.Dependency
import star.intellijplugin.pkgfinder.maven.DependencyService.mavenDownloadUrl
import star.intellijplugin.pkgfinder.maven.LocalDependency
import star.intellijplugin.pkgfinder.maven.NexusDependency
import java.awt.event.MouseEvent

/**
 * Maven Dependency 表格行右键菜单 Action
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-26
 */
object DependencyActionGroup : ActionGroup() {

    private lateinit var selectedDependency: Dependency

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return when (selectedDependency) {
            is CentralDependency -> {
                val centralDependency = selectedDependency as CentralDependency

                // 建立 ec -> downloadUrl 映射
                val ecMap = centralDependency.ec.associateWith { ec ->
                    mavenDownloadUrl(
                        group = centralDependency.groupId,
                        artifactId = centralDependency.artifactId,
                        version = centralDependency.version,
                        ec = ec
                    )
                }

                listOfNotNull(
                    ecMap["-sources.jar"]?.let { DownloadArtifactAction.Companion.forSource(centralDependency, it) },
                    ecMap[".jar"]?.let { DownloadArtifactAction.Companion.forJar(centralDependency, it) },
                    ecMap["-javadoc.jar"]?.let { DownloadArtifactAction.Companion.forJavadoc(centralDependency, it) }
                ).toTypedArray()
            }

            is LocalDependency -> {
                val localDependency = selectedDependency as LocalDependency
                val pomFilePath = localDependency.pomFilePath
                arrayOf(
                    OpenContainingFolderAction(pomFilePath)
                )
            }

            is NexusDependency -> {
                val downloadInfos = (selectedDependency as NexusDependency).downloadInfos
                // 建立 extension -> downloadUrl 映射
                val extMap = downloadInfos.associateBy { it.extension }

                listOfNotNull(
                    // fixme: extension key does not contain sources.jar and javadoc.jar
                    extMap["sources.jar"]?.let {
                        DownloadArtifactAction.Companion.forSource(
                            selectedDependency,
                            it.downloadUrl
                        )
                    },
                    extMap["jar"]?.let { DownloadArtifactAction.Companion.forJar(selectedDependency, it.downloadUrl) },
                    extMap["javadoc.jar"]?.let {
                        DownloadArtifactAction.Companion.forJavadoc(
                            selectedDependency,
                            it.downloadUrl
                        )
                    },
                    extMap["pom"]?.let { DownloadArtifactAction.Companion.forPom(selectedDependency, it.downloadUrl) },
                    extMap["module"]?.let {
                        DownloadArtifactAction.Companion.forModule(
                            selectedDependency,
                            it.downloadUrl
                        )
                    },
                ).toTypedArray()
            }

            else -> emptyArray()
        }
    }

    fun showContextMenu(e: MouseEvent, selectedDependency: Dependency) {
        this.selectedDependency = selectedDependency
        val popupMenu =
            ActionManager.getInstance().createActionPopupMenu("DependencyContextMenu", DependencyActionGroup)
        popupMenu.component.show(e.component, e.x, e.y)
    }
}
