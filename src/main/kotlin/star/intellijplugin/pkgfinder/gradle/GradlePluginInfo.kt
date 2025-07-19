package star.intellijplugin.pkgfinder.gradle

/**
 * @author drawsta
 * @LastModified: 2025-07-14
 * @since 2025-07-14
 */
data class GradlePluginInfo(
    val pluginName: String,
    val latestVersion: String,
    val releaseDate: String,
    val description: String,
)
