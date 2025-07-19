package star.intellijplugin.pkgfinder.ui

import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.toStringProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLoadingPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.TableView
import star.intellijplugin.pkgfinder.PackageFinderBundle
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ListSelectionModel.SINGLE_SELECTION

/**
 * 通用分页表格视图
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-27
 */
abstract class PaginatedTable<T>(val tableModel: PaginatedListTableModel<T>) {
    private val myDisposable = Disposer.newDisposable()
    private val loadingPanel = JBLoadingPanel(BorderLayout(), myDisposable)

    private val myPaginationPanelVisible = AtomicBooleanProperty(false)

    private val propertyGraph: PropertyGraph = PropertyGraph()
    private var currentPageProperty = propertyGraph.lazyProperty { tableModel.getCurrentPage() }
    private var totalPageProperty = propertyGraph.lazyProperty { tableModel.getTotalPages() }

    // 修改 currentPage，就会反应在绑定了 currentPageProperty 的 UI 组件上
    private var currentPage: Int by currentPageProperty
    private var totalPage: Int by totalPageProperty

    fun dispose() {
        Disposer.dispose(myDisposable)
    }

    fun createTablePanel(): DialogPanel {
        return panel {
            val tableView: TableView<T> = createTableView()
            val toolbarDecorator = ToolbarDecorator.createDecorator(tableView)
                .disableAddAction()
                .disableRemoveAction()
                .disableUpDownActions()
            val tablePanel = toolbarDecorator.createPanel()

            // loadingPanel 包裹 tablePanel
            loadingPanel.add(tablePanel, BorderLayout.CENTER)

            row {
                cell(loadingPanel).align(Align.FILL)
            }.resizableRow()

            // 分页条
            val paginationPanel = createPaginationPanel()
            row {
                cell(paginationPanel).align(Align.CENTER)
            }.visibleIf(myPaginationPanelVisible) // 默认不可见，只有表格有数据时才显示
        }
    }

    private fun createTableView(): TableView<T> {
        return TableView(tableModel).apply {
            setShowColumns(true)
            setSelectionMode(SINGLE_SELECTION)

            columnModel.getColumn(0).preferredWidth = 150
            columnModel.getColumn(0).maxWidth = 250

            // 表格中的鼠标点击事件监听
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    mouseClickedInTable(e, selectedRow)
                }
            })
        }
    }

    abstract fun mouseClickedInTable(e: MouseEvent?, selectedRow: Int)

    open fun createPaginationPanel(): DialogPanel {
        return panel {
            row {
                // 上一页
                button(PackageFinderBundle.message("table.pagination.previous")) {
                    if (currentPage > 1) {
                        refreshTable(--currentPage)
                    }
                }
                // 当前页
                label("1").bindText(currentPageProperty.toStringProperty())
                label("/")
                // 总页数
                label("1").bindText(totalPageProperty.toStringProperty())
                // 下一页
                button(PackageFinderBundle.message("table.pagination.next")) {
                    if (currentPage < tableModel.getTotalPages()) {
                        refreshTable(++currentPage)
                    }
                }
            }
        }
    }

    fun refreshTable(page: Int) {
        // 更新表格数据模型中的当前页
        tableModel.setCurrentPage(page)
        // 更新表格数据模型中的当前页数据
        tableModel.updateCurrentPageData()
        // 通知分页条更新
        notifyPaginationChanged(page)
    }

    fun showLoading(withLoading: Boolean) {
        if (withLoading) {
            // 显示加载动画
            loadingPanel.startLoading()
        } else {
            // 隐藏加载动画
            loadingPanel.stopLoading()
        }
    }

    private fun notifyPaginationChanged(page: Int) {
        if (tableModel.getCurrentPageData().isNotEmpty()) {
            // 表格当前页数据不为空，才设置分页条可见
            myPaginationPanelVisible.set(true)

            // 通知 currentPageProperty 和 totalPageProperty，分页信息（当前页、总页数）发生变化
            currentPage = page
            totalPage = tableModel.getTotalPages()
        } else {
            myPaginationPanelVisible.set(false)
        }
    }
}
