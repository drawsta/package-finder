package star.intellijplugin.pkgfinder.setting

import star.intellijplugin.pkgfinder.maven.DependencyFormat
import star.intellijplugin.pkgfinder.maven.DependencyScope
import star.intellijplugin.pkgfinder.maven.MavenRepositorySource

/**
 * @author drawsta
 * @LastModified: 2025-09-07
 * @since 2025-07-08
 */
data class PackageFinderSettingState(
    var nexusServerUrl: String = "",
    var repoSource: MavenRepositorySource = MavenRepositorySource.CENTRAL,
    var dependencyScope: DependencyScope = DependencyScope.COMPILE,
    var dependencyFormat: DependencyFormat = DependencyFormat.GradleGroovyDeclaration
)
