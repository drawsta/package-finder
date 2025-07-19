package star.intellijplugin.pkgfinder.ui.maven

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.SearchTextField
import com.intellij.ui.SideBorder
import com.intellij.ui.components.TextComponentEmptyText
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.NamedColorUtil
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.maven.*
import star.intellijplugin.pkgfinder.ui.PackageFinderListCellRenderer
import star.intellijplugin.pkgfinder.ui.borderPanel
import star.intellijplugin.pkgfinder.ui.boxPanel
import star.intellijplugin.pkgfinder.ui.scrollPanel
import star.intellijplugin.pkgfinder.util.Icons
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

/**
 * refer com.jetbrains.python.packaging.toolwindow.packages.table.PyPackagesTable
 *
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-01-16
 */
class MavenToolWindow(parentDisposable: Disposable) {
    val contentPanel: JPanel
    private val mavenTable = MavenTable()
    private val searchTextField: SearchTextField

    private val propertyGraph: PropertyGraph = PropertyGraph()
    private var repoSourceProperty = propertyGraph.lazyProperty { MavenRepositorySource.CENTRAL }
    private var repoSource: MavenRepositorySource by repoSourceProperty
    private var dependencyFormatProperty = propertyGraph.lazyProperty { DependencyFormat.GradleGroovyDeclaration }
    private var dependencyScopeProperty = propertyGraph.lazyProperty { DependencyScope.COMPILE }

    private val dependencyCache: DependencyCache = DependencyCache()

    init {
        searchTextField = createSearchTextField()

        contentPanel = borderPanel {
            val scrollPanel = scrollPanel {
                val dialogPanel = createPanel()
                viewport.view = dialogPanel
            }
            add(scrollPanel, BorderLayout.CENTER)
        }

        // 资源清理
        Disposer.register(parentDisposable) {
            mavenTable.dispose()
        }
    }

    private fun createPanel(): JPanel {
        return borderPanel {
            val topToolbar = boxPanel {
                border = SideBorder(NamedColorUtil.getBoundsColor(), SideBorder.BOTTOM)
                // 设置 topToolbar 的高度为 50
                preferredSize = Dimension(preferredSize.width, 50)
                minimumSize = Dimension(minimumSize.width, 50)
                maximumSize = Dimension(maximumSize.width, 50)

                add(searchTextField)
                // 间距
                add(Box.createRigidArea(Dimension(10, 0)))

                add(repositoryAndDependencyFormatPanel())

                val actionToolbar = createToolbar(this)
                add(actionToolbar.component)
            }
            add(topToolbar, BorderLayout.NORTH)
            val component = mavenTable.createTablePanel()
            add(component, BorderLayout.CENTER)
        }
    }

    private fun createSearchTextField(): SearchTextField {
        return SearchTextField().apply {
            // placeholder
            textEditor.emptyText.text = message("maven.table.searchField.emptyText")
            // 聚焦搜索框时，使 placeholder 可见，不加这一行，就只能在非聚焦状态可见
            TextComponentEmptyText.setupPlaceholderVisibility(textEditor)

            // 宽度
            preferredSize = Dimension(450, preferredSize.height)
            minimumSize = Dimension(450, minimumSize.height)
            maximumSize = Dimension(450, maximumSize.height)

            addKeyboardListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent) {
                    // 监听回车键，搜索框回车触发搜索
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        handleSearch(text.trim())
                    }
                }
            })

            addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(p0: DocumentEvent) {
                    // 点击 SearchTextField 搜索框右边的清空按钮时
                    if (textEditor.text.isEmpty()) {
                        refreshTable()
                    }
                }
            })
        }
    }

    private fun createToolbar(panel: JPanel): ActionToolbar {
        val group = DefaultActionGroup()

        // 重/加载指定 Maven 仓库来源的包数据
        // 鼠标光标停留在 action 上时，text 为光标的悬浮提示，description 显示在 IDE 左下角
        val reloadIconButtonAction = object : DumbAwareAction(
            message("maven.table.ReloadButton.text"),
            message("maven.table.ReloadButton.description"),
            Icons.REFRESH.getThemeBasedIcon()
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                // 清空搜索框
                searchTextField.text = ""

                lodadAndCacheLocalDependencies()
                refreshTable()
            }

            override fun update(e: AnActionEvent) {
                // 当仓库来源选择本地仓库时，重新加载数据的按钮才可见
                e.presentation.isEnabledAndVisible = repoSource == MavenRepositorySource.LOCAL
            }

            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }
        }

        group.add(reloadIconButtonAction)

        val actionToolbar =
            ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, group, true)
        actionToolbar.targetComponent = panel
        actionToolbar.component.maximumSize = Dimension(70, actionToolbar.component.maximumSize.height)
        // actionToolbar.layoutStrategy = ToolbarLayoutStrategy.NOWRAP_STRATEGY
        return actionToolbar
    }

    private fun repositoryAndDependencyFormatPanel(): DialogPanel {
        return panel {
            row {
                // Maven 仓库来源 https://mvnrepository.com/repos
                comboBox(MavenRepositorySource.entries, PackageFinderListCellRenderer)
                    .label(message("maven.RepositorySource.label"))
                    .bindItem(repoSourceProperty)
                    .onChanged {
                        // 切换仓库来源后，还原表格及分页条到切换前的状态
                        refreshTable(it.item)
                    }
                // 依赖作用域
                comboBox(DependencyScope.entries, PackageFinderListCellRenderer)
                    .label(message("maven.DependencyScope.label"))
                    .bindItem(dependencyScopeProperty)
                    .onChanged {
                        // 切换依赖作用域后，修改表格模型中的依赖作用域成员
                        mavenTable.dependencyScope = it.item
                    }
                // 依赖声明格式
                comboBox(DependencyFormat.entries, PackageFinderListCellRenderer)
                    .label(message("maven.DependencyFormat.label"))
                    .bindItem(dependencyFormatProperty)
                    .onChanged {
                        // 切换依赖声明格式后，修改表格模型中的依赖声明成员
                        mavenTable.dependencyFormat = it.item
                    }
            }
        }
    }

    private fun handleSearch(text: String) {
        mavenTable.showLoading(true)

        ApplicationManager.getApplication().executeOnPooledThread {
            val searchResult: List<Dependency> = when (repoSource) {
                MavenRepositorySource.CENTRAL -> {
                    val dependencies = DependencyService.searchFromMavenCentral(text)
                    cacheCentralDependencies(dependencies)
                    dependencies
                }

                MavenRepositorySource.LOCAL -> {
                    if (dependencyCache.localDependencies.isEmpty()) {
                        lodadAndCacheLocalDependencies()
                    }
                    dependencyCache.localDependencies.filter {
                        val localDependency = it as LocalDependency
                        localDependency.artifactId.contains(text) || localDependency.groupId.contains(text)
                            || localDependency.name.contains(text) || localDependency.description.contains(text)
                    }
                }

                MavenRepositorySource.NEXUS -> {
                    val dependencies = DependencyService.searchFromNexus(text)
                    cacheNexusDependencies(dependencies)
                    dependencies
                }
            }
            ApplicationManager.getApplication().invokeLater {
                // 刷新表格，更新表格所有数据为搜索结果
                mavenTable.refreshTable(searchResult, repoSource)
                mavenTable.showLoading(false)
            }
        }
    }

    private fun lodadAndCacheLocalDependencies() {
        val dependencies: List<Dependency> = LocalDependency.loadData()
        dependencyCache.localDependencies = dependencies
    }

    private fun cacheCentralDependencies(data: List<Dependency>) {
        dependencyCache.centralDependencies = data
    }

    private fun cacheNexusDependencies(data: List<Dependency>) {
        dependencyCache.nexusDependencies = data
    }

    private fun refreshTable(selectedRepoSource: MavenRepositorySource = repoSource) {
        // 还原（清空搜索时，还原表格到搜索前的状态），或更新（执行搜索，或加载本地 Maven 依赖包信息时）表格所有数据
        // 更新了所有数据，同时也是更新当前页数据
        when (selectedRepoSource) {
            MavenRepositorySource.CENTRAL -> {
                searchTextField.textEditor.emptyText.text = message("maven.table.searchField.emptyText")
                mavenTable.refreshTable(dependencyCache.centralDependencies, selectedRepoSource)
            }

            MavenRepositorySource.LOCAL -> {
                searchTextField.textEditor.emptyText.text =
                    message("maven.table.local.searchField.emptyText")
                mavenTable.refreshTable(dependencyCache.localDependencies, selectedRepoSource)
            }

            MavenRepositorySource.NEXUS -> {
                searchTextField.textEditor.emptyText.text =
                    message("maven.table.nexus.searchField.emptyText")
                mavenTable.refreshTable(dependencyCache.nexusDependencies, selectedRepoSource)
            }
        }
    }
}
