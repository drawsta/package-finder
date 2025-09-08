package star.intellijplugin.pkgfinder.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import star.intellijplugin.pkgfinder.maven.Dependency

/**
 * @author drawsta
 * @LastModified: 2025-09-08
 * @since 2025-09-08
 */
class SearchInMavenRepositoryAction(
    private val dependency: Dependency
) : AnAction("Search in Maven Repository") {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse("https://mvnrepository.com/artifact/${dependency.groupId}/${dependency.artifactId}")
    }
}
