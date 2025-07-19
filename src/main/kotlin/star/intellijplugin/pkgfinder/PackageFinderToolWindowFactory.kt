package star.intellijplugin.pkgfinder

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import star.intellijplugin.pkgfinder.ui.gradle.GradlePluginToolWindow
import star.intellijplugin.pkgfinder.ui.maven.MavenToolWindow
import star.intellijplugin.pkgfinder.ui.npm.NpmToolWindow
import star.intellijplugin.pkgfinder.util.Icons

/**
 * @author drawsta
 * @LastModified: 2025-07-14
 * @since 2025-01-16
 */
class PackageFinderToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val contentFactory = contentManager.factory

        val mavenContent = contentFactory.createContent(
            MavenToolWindow(toolWindow.disposable).contentPanel,
            PackageFinderBundle.message("toolwindow.maven.title"),
            false
        ).apply {
            icon = Icons.MAVEN.getThemeBasedIcon()
            putUserData(ToolWindow.SHOW_CONTENT_ICON, true)
        }

        val npmContent = contentFactory.createContent(
            NpmToolWindow(toolWindow.disposable).contentPanel,
            PackageFinderBundle.message("toolwindow.npm.title"),
            false
        ).apply {
            icon = Icons.NPM.getThemeBasedIcon()
            putUserData(ToolWindow.SHOW_CONTENT_ICON, true)
        }

        val gradlePluginContent = contentFactory.createContent(
            GradlePluginToolWindow(toolWindow.disposable).contentPanel,
            PackageFinderBundle.message("toolwindow.gradlePlugin.title"),
            false
        ).apply {
            icon = Icons.GRADLE_PLUGIN.getThemeBasedIcon()
            putUserData(ToolWindow.SHOW_CONTENT_ICON, true)
        }

        contentManager.addContent(mavenContent)
        contentManager.addContent(gradlePluginContent)
        contentManager.addContent(npmContent)
    }
}
