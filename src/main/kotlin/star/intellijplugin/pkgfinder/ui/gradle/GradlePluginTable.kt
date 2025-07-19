package star.intellijplugin.pkgfinder.ui.gradle

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import star.intellijplugin.pkgfinder.PackageFinderBundle
import star.intellijplugin.pkgfinder.gradle.GradlePluginInfo
import star.intellijplugin.pkgfinder.gradle.GradlePluginPortalService
import star.intellijplugin.pkgfinder.ui.PaginatedTable
import java.awt.event.MouseEvent

/**
 * @author drawsta
 * @LastModified: 2025-07-14
 * @since 2025-07-14
 */
class GradlePluginTable : PaginatedTable<GradlePluginInfo>(GradlePluginInfoTableModel()) {

    private val propertyGraph: PropertyGraph = PropertyGraph()
    private val prevHrefProperty: GraphProperty<String> = propertyGraph.property("")
    private val nextHrefProperty: GraphProperty<String> = propertyGraph.property("")
    private var prevHref: String by prevHrefProperty
    private var nextHref: String by nextHrefProperty
    private val hasPrevHrefProperty: GraphProperty<Boolean> = propertyGraph.property(false)
    private val hasNextHrefProperty: GraphProperty<Boolean> = propertyGraph.property(false)

    override fun mouseClickedInTable(e: MouseEvent?, selectedRow: Int) {
    }

    override fun createPaginationPanel(): DialogPanel {
        return panel {
            row {
                // 上一页
                button(PackageFinderBundle.message("table.pagination.previous")) {
                    if (prevHref.isNotEmpty()) {
                        loadPageData(prevHref)
                    }
                }.visibleIf(hasPrevHrefProperty)
                // 下一页
                button(PackageFinderBundle.message("table.pagination.next")) {
                    if (nextHref.isNotEmpty()) {
                        loadPageData(nextHref)
                    }
                }.visibleIf(hasNextHrefProperty)
            }
        }
    }

    fun refreshTable(data: Triple<List<GradlePluginInfo>, String, String>) {
        tableModel.updateTableData(data.first)
        refreshTable(1)

        // 更新上一页、下一页 page link
        prevHref = data.second
        nextHref = data.third

        hasPrevHrefProperty.set(prevHref.isNotEmpty())
        hasNextHrefProperty.set(nextHref.isNotEmpty())
    }

    private fun loadPageData(pageLink: String) {
        showLoading(true)
        ApplicationManager.getApplication().executeOnPooledThread {
            val pluginInfoTriple = GradlePluginPortalService.searchPage(pageLink)
            ApplicationManager.getApplication().invokeLater {
                // 刷新表格，更新表格所有数据为搜索结果
                refreshTable(pluginInfoTriple)
                showLoading(false)
            }
        }
    }
}
