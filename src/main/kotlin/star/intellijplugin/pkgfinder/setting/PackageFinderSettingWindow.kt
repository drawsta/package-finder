package star.intellijplugin.pkgfinder.setting

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import star.intellijplugin.pkgfinder.PackageFinderBundle

/**
 * @author drawsta
 * @LastModified: 2025-07-13
 * @since 2025-07-08
 */
class PackageFinderSettingWindow : BoundConfigurable(
    PackageFinderBundle.message("settings.name")
) {

    private val setting = PackageFinderSetting.instance

    override fun createPanel(): DialogPanel {
        return panel {
            group(PackageFinderBundle.message("settings.general.group.title")) {
                row(PackageFinderBundle.message("settings.nexusServerUrl")) {
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
