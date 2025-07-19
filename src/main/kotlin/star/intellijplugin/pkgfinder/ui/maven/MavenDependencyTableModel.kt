package star.intellijplugin.pkgfinder.ui.maven

import com.intellij.util.ui.ColumnInfo
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.maven.CentralDependency
import star.intellijplugin.pkgfinder.maven.Dependency
import star.intellijplugin.pkgfinder.maven.LocalDependency
import star.intellijplugin.pkgfinder.maven.NexusDependency
import star.intellijplugin.pkgfinder.ui.PaginatedListTableModel
import star.intellijplugin.pkgfinder.ui.TableColumnInfo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Maven 依赖包信息表格的数据模型
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-20
 */
class MavenDependencyTableModel : PaginatedListTableModel<Dependency>() {

    private val localTableColumnInfos: Array<ColumnInfo<LocalDependency, Any>> = arrayOf(
        TableColumnInfo(message("maven.table.column.artifactId")) { it.artifactId },
        TableColumnInfo(message("maven.table.column.groupId")) { it.groupId },
        TableColumnInfo(message("maven.table.column.version")) { it.version },
        TableColumnInfo(message("maven.table.column.name")) { it.name },
        TableColumnInfo(message("maven.table.column.description")) { it.description }
    )

    private val centralTableColumnInfos: Array<ColumnInfo<CentralDependency, Any>> = arrayOf(
        TableColumnInfo(message("maven.table.column.artifactId")) { it.artifactId },
        TableColumnInfo(message("maven.table.column.groupId")) { it.groupId },
        TableColumnInfo(message("maven.table.column.version")) { it.version },
        TableColumnInfo(message("maven.table.column.packaging")) { it.packaging },
        TableColumnInfo(message("maven.table.column.date")) { it.timestamp.toDateString() }
    )

    private val nexusTableColumnInfos: Array<ColumnInfo<NexusDependency, Any>> = arrayOf(
        TableColumnInfo(message("maven.table.column.artifactId")) { it.artifactId },
        TableColumnInfo(message("maven.table.column.groupId")) { it.groupId },
        TableColumnInfo(message("maven.table.column.version")) { it.version },
        TableColumnInfo(message("maven.table.column.uploaderIp")) { it.uploaderIp },
    )

    init {
        columnInfos = centralTableColumnInfos
    }

    fun switchToLocalDependencyColumnInfo() {
        columnInfos = localTableColumnInfos
        fireTableStructureChanged()
    }

    fun switchToCentralDependencyColumnInfo() {
        columnInfos = centralTableColumnInfos
        fireTableStructureChanged()
    }

    fun switchToNexusDependencyColumnInfo() {
        columnInfos = nexusTableColumnInfos
        fireTableStructureChanged()
    }
}

private fun Long.toDateString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    return Instant.ofEpochMilli(this)
        .atZone(zoneId)
        .toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
}
