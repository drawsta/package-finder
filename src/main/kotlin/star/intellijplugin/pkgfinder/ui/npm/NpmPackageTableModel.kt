package star.intellijplugin.pkgfinder.ui.npm

import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.npm.NpmObject
import star.intellijplugin.pkgfinder.ui.PaginatedListTableModel
import star.intellijplugin.pkgfinder.ui.TableColumnInfo

/**
 * Npm Package 表格的数据模型
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-27
 */
class NpmPackageTableModel : PaginatedListTableModel<NpmObject>() {
    init {
        columnInfos = arrayOf(
            TableColumnInfo<NpmObject>(message("npm.table.column.packageName")) { it.`package`.packageName },
            TableColumnInfo(message("npm.table.column.version")) { it.`package`.version },
            TableColumnInfo(message("npm.table.column.date")) { it.`package`.date },
            TableColumnInfo(message("npm.table.column.publisher")) {
                it.`package`.publisher?.username ?: message("common.NotAvailable")
            },
            TableColumnInfo(message("npm.table.column.description")) { it.`package`.description },
            TableColumnInfo(message("npm.table.column.license")) { it.`package`.license },
            TableColumnInfo(message("npm.table.column.MonthlyDownloads")) { it.downloads.monthly },
        )
    }
}
