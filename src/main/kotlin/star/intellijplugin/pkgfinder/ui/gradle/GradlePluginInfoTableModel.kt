package star.intellijplugin.pkgfinder.ui.gradle

import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.gradle.GradlePluginInfo
import star.intellijplugin.pkgfinder.ui.TableColumnInfo
import star.intellijplugin.pkgfinder.ui.PaginatedListTableModel

/**
 * @author drawsta
 * @LastModified: 2025-07-14
 * @since 2025-07-14
 */
class GradlePluginInfoTableModel : PaginatedListTableModel<GradlePluginInfo>() {
    init {
        columnInfos = arrayOf(
            TableColumnInfo<GradlePluginInfo>(message("GradlePlugin.table.column.pluginName")) { it.pluginName },
            TableColumnInfo(message("GradlePlugin.table.column.latestVersion")) { it.latestVersion },
            TableColumnInfo(message("GradlePlugin.table.column.releaseDate")) { it.releaseDate },
            TableColumnInfo(message("GradlePlugin.table.column.description")) { it.description },
        )
    }
}
