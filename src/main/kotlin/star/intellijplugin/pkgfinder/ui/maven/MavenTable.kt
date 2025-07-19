package star.intellijplugin.pkgfinder.ui.maven

import com.intellij.codeInsight.hints.presentation.MouseButton
import com.intellij.openapi.ide.CopyPasteManager
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.action.DependencyActionGroup
import star.intellijplugin.pkgfinder.maven.Dependency
import star.intellijplugin.pkgfinder.maven.DependencyFormat
import star.intellijplugin.pkgfinder.maven.DependencyScope
import star.intellijplugin.pkgfinder.maven.MavenRepositorySource
import star.intellijplugin.pkgfinder.ui.PaginatedTable
import star.intellijplugin.pkgfinder.util.showInformationNotification
import java.awt.event.MouseEvent

/**
 * Maven 依赖包信息表格
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-18
 */
class MavenTable : PaginatedTable<Dependency>(MavenDependencyTableModel()) {

    var dependencyFormat: DependencyFormat = DependencyFormat.GradleGroovyDeclaration

    var dependencyScope: DependencyScope = DependencyScope.COMPILE

    override fun mouseClickedInTable(e: MouseEvent?, selectedRow: Int) {
        // 获取当前选中行的数据
        val selectedDependency = tableModel.getItem(selectedRow)

        // 鼠标左键双击表格行时，直接复制依赖声明
        if (e?.clickCount == 2) {
            // 复制选中的依赖到剪贴板
            copyDependencyToClipboard(selectedDependency)
        }
        // 鼠标右键时，显示一个菜单
        if (e?.let { MouseButton.fromEvent(it) } == MouseButton.Right) {
            DependencyActionGroup.showContextMenu(e, selectedDependency)
        }
    }

    fun refreshTable(data: List<Dependency>, mavenRepositorySource: MavenRepositorySource) {
        tableModel as MavenDependencyTableModel
        when (mavenRepositorySource) {
            MavenRepositorySource.CENTRAL -> tableModel.switchToCentralDependencyColumnInfo()
            MavenRepositorySource.LOCAL -> tableModel.switchToLocalDependencyColumnInfo()
            MavenRepositorySource.NEXUS -> tableModel.switchToNexusDependencyColumnInfo()
        }

        tableModel.updateTableData(data)
        refreshTable(1)
    }

    private fun copyDependencyToClipboard(dependency: Dependency) {
        when (dependencyFormat) {
            DependencyFormat.GradleGroovyDeclaration ->
                CopyPasteManager.copyTextToClipboard(dependency.getGradleGroovyDeclaration(dependencyScope))

            DependencyFormat.GradleKotlinDeclaration ->
                CopyPasteManager.copyTextToClipboard(dependency.getGradleKotlinDeclaration(dependencyScope))

            DependencyFormat.MavenDeclaration ->
                CopyPasteManager.copyTextToClipboard(dependency.getMavenDeclaration(dependencyScope))
        }

        showInformationNotification(message("notification.copyToClipboard"))
    }
}
