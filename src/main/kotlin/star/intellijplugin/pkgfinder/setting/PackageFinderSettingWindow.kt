package star.intellijplugin.pkgfinder.setting

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import star.intellijplugin.pkgfinder.PackageFinderBundle.message
import star.intellijplugin.pkgfinder.maven.DependencyFormat
import star.intellijplugin.pkgfinder.maven.DependencyScope
import star.intellijplugin.pkgfinder.maven.MavenRepositorySource
import star.intellijplugin.pkgfinder.ui.PackageFinderListCellRenderer

/**
 * @author drawsta
 * @LastModified: 2025-09-07
 * @since 2025-07-08
 */
class PackageFinderSettingWindow : BoundConfigurable(
    message("settings.name")
) {

    private val setting = PackageFinderSetting.instance

    override fun createPanel(): DialogPanel {
        return panel {
            group(message("settings.maven.group.title")) {
                row(message("settings.maven.repoSource")) {
                    comboBox(MavenRepositorySource.entries, PackageFinderListCellRenderer)
                        .bindItem(
                            getter = { setting.repoSource },
                            setter = { setting.repoSource = it!! }
                        )
                }
                row(message("settings.maven.dependencyScope")) {
                    comboBox(DependencyScope.entries, PackageFinderListCellRenderer)
                        .bindItem(
                            getter = { setting.dependencyScope },
                            setter = { setting.dependencyScope = it!! }
                        )
                }
                row(message("settings.maven.dependencyFormat")) {
                    comboBox(DependencyFormat.entries, PackageFinderListCellRenderer)
                        .bindItem(
                            getter = { setting.dependencyFormat },
                            setter = { setting.dependencyFormat = it!! }
                        )
                }
            }

            group(message("settings.nexus.group.title")) {
                row(message("settings.nexus.nexusServerUrl")) {
                    textField()
                        .bindText(
                            getter = { setting.nexusServerUrl },
                            setter = { setting.nexusServerUrl = it }
                        )
                }
            }
        }
    }
}
