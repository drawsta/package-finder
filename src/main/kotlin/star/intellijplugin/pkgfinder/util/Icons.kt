package star.intellijplugin.pkgfinder.util

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import javax.swing.Icon

/**
 * @author drawsta
 * @LastModified: 2025-07-14
 * @since 2025-01-23
 */
object Icons {

    val MAVEN = PackageFinderIcon("/icons/maven.svg", "/icons/maven_dark.svg")
    val NPM = PackageFinderIcon("/icons/npm.svg", "/icons/npm_dark.svg")
    val GRADLE_PLUGIN = PackageFinderIcon("/icons/gradlePlugin.svg", "/icons/gradlePlugin_dark.svg")
    val LOCAL_REPOSITORY = PackageFinderIcon("/icons/localRepository.svg", "/icons/localRepository_dark.svg")
    val CENTRAL_REPOSITORY = PackageFinderIcon("/icons/centralRepository.svg", "/icons/centralRepository_dark.svg")
    val NEXUS_REPOSITORY = PackageFinderIcon("/icons/nexus.svg", "/icons/nexus.svg")
    val REFRESH = PackageFinderIcon("/icons/refresh.svg", "/icons/refresh_dark.svg")
    val JAR = PackageFinderIcon("/icons/jar.svg", null)
    val JAR_SOURCES = PackageFinderIcon("/icons/jar_sources.svg", null)
    val JAVADOC = PackageFinderIcon("/icons/javadoc.svg", null)
    val FOLDER = PackageFinderIcon("/icons/folder.svg", "/icons/folder_dark.svg")

    data class PackageFinderIcon(val bright: String, val dark: String?) {

        fun getThemeBasedIcon(): Icon {
            return if (JBColor.isBright() || dark == null) {
                IconLoader.getIcon(bright, javaClass)
            } else {
                IconLoader.getIcon(dark, javaClass)
            }
        }
    }
}
