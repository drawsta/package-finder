package star.intellijplugin.pkgfinder.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger

/**
 * @author drawsta
 * @LastModified: 2025-07-08
 * @since 2025-07-08
 */
@Service(Service.Level.APP)
@State(name = "PackageFinder", storages = [Storage("PackageFinder-config.xml")])
class PackageFinderSetting : PersistentStateComponent<PackageFinderSettingState> {

    private var myState = PackageFinderSettingState()

    private val log = Logger.getInstance(javaClass)

    companion object {
        val instance: PackageFinderSetting
            get() = ApplicationManager.getApplication().getService(PackageFinderSetting::class.java)
    }

    var nexusServerUrl: String
        get() = myState.nexusServerUrl
        set(value) {
            myState.nexusServerUrl = value
            log.info("Nexus server url updated to $value")
        }

    override fun getState(): PackageFinderSettingState? {
        return myState
    }

    override fun loadState(state: PackageFinderSettingState) {
        myState = state
    }
}
