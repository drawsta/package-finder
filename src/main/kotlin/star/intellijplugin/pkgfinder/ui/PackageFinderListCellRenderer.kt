package star.intellijplugin.pkgfinder.ui

import star.intellijplugin.pkgfinder.maven.DependencyFormat
import star.intellijplugin.pkgfinder.maven.DependencyScope
import star.intellijplugin.pkgfinder.maven.MavenRepositorySource
import star.intellijplugin.pkgfinder.npm.NpmPackageManager
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

/**
 * @author drawsta
 * @LastModified: 2025-07-18
 * @since 2025-01-23
 */
object PackageFinderListCellRenderer : DefaultListCellRenderer() {

    // DefaultListCellRenderer 实现了 Serializable 接口，实现 readResolve 方法，确保反序列化时仍然返回单例实例
    @Suppress
    private fun readResolve(): Any = PackageFinderListCellRenderer

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val component = super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus
        )
        when (value) {
            is MavenRepositorySource -> {
                text = value.displayName
                icon = value.icon
            }

            is DependencyFormat -> {
                text = value.displayName
            }

            is DependencyScope -> {
                text = value.displayName
            }

            is NpmPackageManager -> {
                text = value.displayName
            }
        }
        return component
    }
}
