package star.intellijplugin.pkgfinder.maven

import star.intellijplugin.pkgfinder.util.Icons
import javax.swing.Icon

/**
 * Maven 仓库来源
 *
 * @author drawsta
 * @LastModified: 2025-07-07
 * @since 2025-01-23
 */
enum class MavenRepositorySource(val displayName: String, val icon: Icon) {
    CENTRAL("Central", Icons.CENTRAL_REPOSITORY.getThemeBasedIcon()),
    LOCAL("Local", Icons.LOCAL_REPOSITORY.getThemeBasedIcon()),
    NEXUS("Nexus", Icons.NEXUS_REPOSITORY.getThemeBasedIcon())
}
