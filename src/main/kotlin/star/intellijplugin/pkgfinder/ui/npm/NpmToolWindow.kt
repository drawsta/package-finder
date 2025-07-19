package star.intellijplugin.pkgfinder.ui.npm

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.SearchTextField
import com.intellij.ui.SideBorder
import com.intellij.ui.components.TextComponentEmptyText
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.NamedColorUtil
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.npm.NpmPackageManager
import star.intellijplugin.pkgfinder.npm.NpmRegistryService
import star.intellijplugin.pkgfinder.ui.PackageFinderListCellRenderer
import star.intellijplugin.pkgfinder.ui.borderPanel
import star.intellijplugin.pkgfinder.ui.boxPanel
import star.intellijplugin.pkgfinder.ui.scrollPanel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.JPanel

/**
 * @author drawsta
 * @LastModified: 2025-07-18
 * @since 2025-01-27
 */
class NpmToolWindow(parentDisposable: Disposable) {
    // view
    val contentPanel: JPanel
    private val npmTable = NpmTable()
    private val searchTextField: SearchTextField

    private val propertyGraph: PropertyGraph = PropertyGraph()
    private var packageManagerProperty = propertyGraph.lazyProperty { NpmPackageManager.NPM }

    init {
        searchTextField = createSearchTextField()

        contentPanel = borderPanel {
            val scrollPanel = scrollPanel {
                viewport.view = borderPanel {
                    val topToolbar = boxPanel {
                        border = SideBorder(NamedColorUtil.getBoundsColor(), SideBorder.BOTTOM)
                        // 设置 topToolbar 的高度为 50
                        preferredSize = Dimension(preferredSize.width, 50)
                        minimumSize = Dimension(minimumSize.width, 50)
                        maximumSize = Dimension(maximumSize.width, 50)
                        add(searchTextField)
                        add(Box.createRigidArea(Dimension(10, 0)))
                        add(packageManagerPanel())
                    }
                    add(topToolbar, BorderLayout.NORTH)
                    val component = npmTable.createTablePanel()
                    add(component, BorderLayout.CENTER)
                }
            }
            add(scrollPanel, BorderLayout.CENTER)
        }

        // 资源清理
        Disposer.register(parentDisposable) {
            npmTable.dispose()
        }
    }

    private fun createSearchTextField(): SearchTextField {
        return SearchTextField().apply {
            // placeholder
            textEditor.emptyText.text = message("npm.table.searchField.emptyText")
            // 聚焦搜索框时，使 placeholder 可见，不加这一行，就只能在非聚焦状态可见
            TextComponentEmptyText.setupPlaceholderVisibility(textEditor)

            // 宽度
            preferredSize = Dimension(550, preferredSize.height)
            minimumSize = Dimension(550, minimumSize.height)
            maximumSize = Dimension(550, maximumSize.height)

            addKeyboardListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent) {
                    // 监听回车键，搜索框回车触发搜索
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        handleSearch(text.trim())
                    }
                }
            })
        }
    }

    private fun handleSearch(text: String) {
        npmTable.showLoading(true)
        ApplicationManager.getApplication().executeOnPooledThread {
            val npmObjects = NpmRegistryService.search(text)
            ApplicationManager.getApplication().invokeLater {
                // 刷新表格，更新表格所有数据为搜索结果
                npmTable.refreshTable(npmObjects)
                npmTable.showLoading(false)
            }
        }
    }

    private fun packageManagerPanel(): DialogPanel {
        return panel {
            row {
                comboBox(NpmPackageManager.entries, PackageFinderListCellRenderer)
                    .label(message("npm.PackageManager.label"))
                    .bindItem(packageManagerProperty)
                    .onChanged {
                        npmTable.packageManagerName = it.item
                    }
            }
        }
    }
}
